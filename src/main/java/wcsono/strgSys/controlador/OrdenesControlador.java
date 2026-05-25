package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.*;
import jakarta.servlet.http.HttpSession;


import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class OrdenesControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private IOrdenServicio ordenServicio;

    @Autowired
    private ITipoDocumentoServicio tipoDocumentoServicio;

    @Autowired
    private MovimientoServicio movimientoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ClienteServicio clienteServicio;


    private final Logger logger = LoggerFactory.getLogger(OrdenesControlador.class);

    /**
     * Listar todas las órdenes (vista Thymeleaf)
     */
    @GetMapping("/ordenes")
    public String mostrarOrdenes(
            @RequestParam(required = false) String numOrd,
            @RequestParam(required = false) Integer idCliente,   // 👈 usar idCliente
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecOrdDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecOrdHasta,
            @RequestParam(required = false) String estOrd,
            Pageable pageable,
            Model model) {

        Integer estado = null;
        if (estOrd != null && !estOrd.isEmpty()) {
            estado = Integer.parseInt(estOrd);
        }

        Page<Orden> paginaOrdenes = ordenServicio
                .listarOrdenesFiltradas(numOrd, idCliente, fecOrdDesde, fecOrdHasta, estado, pageable);

        model.addAttribute("paginaOrdenes", paginaOrdenes);
        model.addAttribute("listadoOrdenes", paginaOrdenes.getContent());

        return "ordenes";
    }


    /**
     * Mostrar formulario para agregar una nueva orden
     */
    @GetMapping("/agregarOrden")
    public String mostrarAgregarOrden(Model model) {
        model.addAttribute("ordenForma", new Orden());
        model.addAttribute("clienteForma", new Cliente()); // necesario para el modal
        model.addAttribute("tdsAgregarOrden", tipoDocumentoServicio.listarTipoDocumentos());
        logger.info("✅ Preparando formulario de nueva orden");
        return "agregarOrden";
    }

    /**
     * Guardar nueva orden y redirigir a detalle
     */
    @PostMapping("/guardarAgregarOrden")
    public String guardarAgregarOrden(@ModelAttribute("orden") Orden orden,
                                      @RequestParam String codCli,
                                      HttpSession session) {

        // Recuperar usuario desde la sesión
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            return "redirect:/login";
        }

        // Buscar cliente por código
        Cliente clienteExistente = clienteServicio.obtenerClientePorCodigo(codCli);

        if (clienteExistente == null) {
            // Si no existe, redirigir al flujo de registro de cliente (modal)
            return "redirect:/clientes/nuevo?codCli=" + codCli;
        }

        // Asociar cliente existente a la orden
        orden.setCliente(clienteExistente);

        // ⚠️ Asignar numOrd provisional para evitar error NOT NULL
        if (orden.getNumOrd() == null || orden.getNumOrd().isEmpty()) {
            orden.setNumOrd("0");
        }

        // Guardar orden con cliente y usuario
        Orden nuevaOrden = ordenServicio.guardarOrden(orden, clienteExistente, usuarioActivo);

        // Redirigir al detalle de la orden
        return "redirect:/orden/" + nuevaOrden.getIdOrd();
    }

    /**
     * Mostrar detalle de la orden
     */
    @GetMapping("/ordenDetalle/{id}")
    public String mostrarDetalleOrden(@PathVariable Integer id, Model model) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        model.addAttribute("orden", orden);
        model.addAttribute("detalleOrdenes", orden.getDetalles());
        model.addAttribute("totalOrden", BigDecimal.ZERO); // inicial, sin artículos

        return "ordenDetalle";
    }
}
