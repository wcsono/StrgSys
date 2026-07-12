package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.FacturacionServicio;
import wcsono.strgSys.servicio.IOrdenServicio;
import wcsono.strgSys.enums.EstadoOrden;

import jakarta.servlet.http.HttpSession;

@Controller
public class FacturacionControlador {

    private static final Logger logger = LoggerFactory.getLogger(FacturacionControlador.class);

    private final FacturacionServicio facturacionServicio;
    private final IOrdenServicio ordenServicio;

    public FacturacionControlador(FacturacionServicio facturacionServicio, IOrdenServicio ordenServicio) {
        this.facturacionServicio = facturacionServicio;
        this.ordenServicio = ordenServicio;
    }

    // Guardar facturación desde el modal
    @PostMapping("/facturacion/facturar")
    public String guardarFacturacion(@RequestParam("idOrd") Integer idOrd,
                                     @RequestParam("numFactura") String numFactura,
                                     HttpSession session,
                                     RedirectAttributes redirectAttrs) {
        logger.info("Guardando facturación para Orden ID: {}, NumFactura: {}", idOrd, numFactura);

        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            redirectAttrs.addFlashAttribute("error", "No se encontró la orden para facturar.");
            return "redirect:/ordenes";
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        if (numFactura == null || numFactura.trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Debe ingresar un número de factura válido.");
            return "redirect:/ordenes";
        }

        // Registrar factura
        facturacionServicio.registrarFactura(idOrd, numFactura);

        // Actualizar estado y usuario activo
        orden.setEstOrd(EstadoOrden.FACTURADA);
        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje",
                "Orden facturada correctamente por el usuario: " + usuarioActivo.getNombre());

        return "redirect:/ordenes";
    }

    // Listado de facturaciones por orden
    @GetMapping("/orden/{id}/facturaciones")
    public String listarFacturaciones(@PathVariable("id") Integer idOrd, ModelMap modelo) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            modelo.put("error", "No se encontró la orden solicitada.");
            return "fragmentos/error :: mensajeError";
        }
        modelo.put("orden", orden);
        modelo.put("facturaciones", facturacionServicio.buscarPorOrden(idOrd));
        return "facturaciones";
    }
}
