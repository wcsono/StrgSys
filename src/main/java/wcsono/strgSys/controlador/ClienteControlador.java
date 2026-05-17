package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.servicio.IClienteServicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ClienteControlador {

    private static final Logger logger = LoggerFactory.getLogger(ClienteControlador.class);

    @Autowired
    private IClienteServicio clienteServicio;

    // Listar clientes
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteServicio.listarClientes());
        return "clientes";
    }

    // Mostrar formulario para agregar cliente
    @GetMapping("/agregarCliente")
    public String mostrarFormularioAgregar(Model model) {
        model.addAttribute("clienteForma", new Cliente());
        return "agregarCliente";
    }

    // Guardar cliente nuevo
    @PostMapping("/guardarCliente")
    public String guardarCliente(@ModelAttribute("clienteForma") Cliente cliente, Model model) {
        try {
            clienteServicio.guardarCliente(cliente);
            model.addAttribute("mensajeExito", "Cliente agregado correctamente.");
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al agregar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    // Mostrar formulario para editar cliente
    @GetMapping("/editarCliente/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer idCliente, Model model) {
        Cliente cliente = clienteServicio.obtenerClientePorId(idCliente);
        if (cliente != null) {
            model.addAttribute("clienteForma", cliente);
            return "editarCliente";
        } else {
            model.addAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
    }

    // Guardar cambios de cliente editado
    @PostMapping("/actualizarCliente")
    public String actualizarCliente(@ModelAttribute("clienteForma") Cliente cliente, Model model) {
        try {
            clienteServicio.guardarCliente(cliente);
            model.addAttribute("mensajeExito", "Cliente actualizado correctamente.");
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al actualizar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    // 🔹 Nuevo endpoint: buscar cliente por código
    @GetMapping("/clientes/buscarPorCodigo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> buscarPorCodigo(@RequestParam String codCli) {
        Cliente cliente = clienteServicio.obtenerClientePorCodigo(codCli);

        if (cliente != null) {
            // Log del objeto completo
            logger.info("✅ Cliente encontrado: {}", cliente);

            Map<String, String> datos = new HashMap<>();
            datos.put("nomCli", cliente.getNomCli());
            datos.put("dirCli", cliente.getDirCli());
            return ResponseEntity.ok(datos);
        } else {
            logger.warn("⚠️ Cliente con código {} no encontrado", codCli);
            return ResponseEntity.notFound().build();
        }
    }
}
