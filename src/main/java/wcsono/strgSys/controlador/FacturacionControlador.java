package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.servicio.FacturacionServicio;
import wcsono.strgSys.servicio.IOrdenServicio;   // 🔹 usa la interfaz
import wcsono.strgSys.enums.EstadoOrden;

@Controller
public class FacturacionControlador {

    private static final Logger logger = LoggerFactory.getLogger(FacturacionControlador.class);
    private final FacturacionServicio facturacionServicio;
    private final IOrdenServicio ordenServicio;   // 🔹 mejor usar la interfaz

    // Constructor injection
    public FacturacionControlador(FacturacionServicio facturacionServicio, IOrdenServicio ordenServicio) {
        this.facturacionServicio = facturacionServicio;
        this.ordenServicio = ordenServicio;
    }

    // Guardar facturación desde el modal
    @PostMapping("/facturacion/guardar")
    public String guardarFacturacion(@RequestParam("idOrd") Integer idOrd,
                                     @RequestParam("numFactura") String numFactura,
                                     RedirectAttributes redirectAttrs) {
        logger.info("Guardando facturación para Orden ID: {}", idOrd);

        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            logger.warn("Orden no encontrada con ID: {}", idOrd);
            redirectAttrs.addFlashAttribute("error", "No se encontró la orden para facturar.");
            return "redirect:/ordenes";
        }

        // 1️⃣ Registrar la factura
        facturacionServicio.registrarFactura(idOrd, numFactura);

        // 2️⃣ Actualizar estado de la orden a FACTURADA
        ordenServicio.actualizarEstadoOrden(idOrd, EstadoOrden.FACTURADA);

        // 3️⃣ Redirigir al listado
        redirectAttrs.addFlashAttribute("mensaje", "Orden facturada correctamente.");
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
