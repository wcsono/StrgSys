package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Articulo;
import wcsono.strgSys.modelo.DetalleOrden;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.OrdenServicio;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.DetalleOrdenServicio;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orden")
public class OrdenDetalleControlador {

    @Autowired
    private OrdenServicio ordenServicio;

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private DetalleOrdenServicio detalleOrdenServicio;

    // ✅ Agregar artículo a la orden
    @PostMapping("/{idOrd}/articulos")
    public ResponseEntity<Void> agregarArticulo(@PathVariable Integer idOrd,
                                                @RequestParam Integer idArt,
                                                @RequestParam Integer cantidad,
                                                HttpSession session) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        Articulo articulo = articuloServicio.buscarArticuloPorId(idArt);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setArticulo(articulo);
        detalle.setCantidad(cantidad);
        detalle.setCosArt(articulo.getCosto());
        detalle.setPrecioVenta(articulo.getPrecioVenta());

        detalleOrdenServicio.guardarDetalleOrden(detalle);

        // Recalcular costo total de la orden
        BigDecimal totalOrden = detalleOrdenServicio.listarPorOrden(idOrd).stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setCosOrd(totalOrden);

        // 🔹 Cambiar estado: si estaba en INICIAL o DEVUELTA → ABIERTA
        if (orden.getEstOrd() == EstadoOrden.INICIAL || orden.getEstOrd() == EstadoOrden.DEVUELTA) {
            orden.setEstOrd(EstadoOrden.ABIERTA);
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        orden.setUsuario(usuarioActivo);
        orden.setFechaEstado(java.time.LocalDateTime.now());

        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        return ResponseEntity.ok().build();
    }

    // ✅ Eliminar artículo de la orden
    @PostMapping("/{idOrd}/articulos/{idDo}/eliminar")
    public String eliminarArticulo(@PathVariable Integer idOrd,
                                   @PathVariable Integer idDo,
                                   HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            redirectAttrs.addFlashAttribute("error", "Orden no encontrada");
            return "redirect:/ordenes";
        }

        // 🔹 Validar estado permitido (solo ABIERTA o DEVUELTA)
        if (!(orden.getEstOrd() == EstadoOrden.ABIERTA || orden.getEstOrd() == EstadoOrden.DEVUELTA)) {
            redirectAttrs.addFlashAttribute("error",
                    "No se pueden eliminar artículos en estado " + orden.getEstOrd().getDescripcion());
            return "redirect:/orden/editar/" + idOrd;
        }

        // Buscar detalle y eliminar
        DetalleOrden detalle = detalleOrdenServicio.buscarDetalleOrdenPorId(idDo);
        if (detalle != null) {
            detalleOrdenServicio.eliminarDetalleOrden(detalle);
        }

        // Recalcular costo total
        BigDecimal totalOrden = detalleOrdenServicio.listarPorOrden(idOrd).stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setCosOrd(totalOrden);

        // Ajustar estado según artículos restantes
        if (detalleOrdenServicio.listarPorOrden(idOrd).isEmpty()) {
            orden.setEstOrd(EstadoOrden.INICIAL);
        } else {
            orden.setEstOrd(EstadoOrden.ABIERTA);
        }

        // Registrar usuario y fecha de estado
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        orden.setUsuario(usuarioActivo);
        orden.setFechaEstado(java.time.LocalDateTime.now());

        // Actualizar orden con estado, fecha y usuario
        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        // Mensaje para alertas.html
        redirectAttrs.addFlashAttribute("mensaje",
                "✅ Artículo eliminado correctamente. Estado de la Orden pasó a: " + orden.getEstOrd().getDescripcion());

        return "redirect:/orden/editar/" + idOrd;
    }

} // fin de la clase
