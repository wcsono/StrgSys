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
    @PostMapping("/{id}/articulos")
    public ResponseEntity<Void> agregarArticulo(@PathVariable Integer id,
                                                @RequestParam Integer idArt,
                                                @RequestParam Integer cantidad,
                                                HttpSession session) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        Articulo articulo = articuloServicio.buscarArticuloPorId(idArt);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setArticulo(articulo);
        detalle.setCantidad(cantidad);
        detalle.setCosArt(articulo.getCosto());
        detalle.setPrecioVenta(articulo.getPrecioVenta());

        detalleOrdenServicio.guardarDetalleOrden(detalle);

        // Recalcular costo total de la orden
        BigDecimal totalOrden = detalleOrdenServicio.listarPorOrden(id).stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setCosOrd(totalOrden);

        // 🔹 Cambiar estado: si estaba en INICIAL o DEVUELTA → ABIERTA
        if (orden.getEstOrd() == EstadoOrden.INICIAL || orden.getEstOrd() == EstadoOrden.DEVUELTA) {
            orden.setEstOrd(EstadoOrden.ABIERTA);
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        ordenServicio.actualizarOrden(orden, orden.getCliente(), usuarioActivo);

        return ResponseEntity.ok().build();
    }

    // ✅ Eliminar artículo de la orden (permitido si está INICIAL, ABIERTA o DEVUELTA)
    @PostMapping("/{id}/articulos/{idDet}/eliminar")
    public ResponseEntity<String> eliminarArticulo(@PathVariable Integer id,
                                                   @PathVariable Integer idDet,
                                                   HttpSession session) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);

        if (orden.getEstOrd() == EstadoOrden.INICIAL ||
                orden.getEstOrd() == EstadoOrden.ABIERTA ||
                orden.getEstOrd() == EstadoOrden.DEVUELTA) {

            DetalleOrden detalle = detalleOrdenServicio.buscarDetalleOrdenPorId(idDet);
            if (detalle != null) {
                detalleOrdenServicio.eliminarDetalleOrden(detalle);
            }

            // Recalcular costo total
            BigDecimal totalOrden = detalleOrdenServicio.listarPorOrden(id).stream()
                    .map(DetalleOrden::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orden.setCosOrd(totalOrden);

            Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
            ordenServicio.actualizarOrden(orden, orden.getCliente(), usuarioActivo);

            return ResponseEntity.ok("Artículo eliminado correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se puede eliminar artículos de una orden facturada o superior");
        }
    }
}
