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
public class DetalleOrdenControlador {

    @Autowired
    private OrdenServicio ordenServicio;

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private DetalleOrdenServicio detalleOrdenServicio;

    @PostMapping("/{id}/articulos")
    public ResponseEntity<Void> agregarArticulo(@PathVariable Integer id,
                                                @RequestParam Integer idArt,
                                                @RequestParam Integer cantidad,
                                                @RequestParam BigDecimal precio,
                                                HttpSession session) {

        Orden orden = ordenServicio.buscarOrdenPorId(id);
        Articulo articulo = articuloServicio.buscarArticuloPorId(idArt);

        // Crear detalle con subtotal explícito
        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setArticulo(articulo);
        detalle.setCantidad(cantidad);
        detalle.setCosArt(precio);
        detalle.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad))); // ✅ asegura cálculo

        detalleOrdenServicio.guardarDetalleOrden(detalle);

        // Recalcular costo total
        BigDecimal totalOrden = detalleOrdenServicio.listarPorOrden(id).stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setCosOrd(totalOrden);

        // Cambiar estado: si estaba en INICIAL, pasa a ABIERTA
        if (orden.getEstOrd() == EstadoOrden.INICIAL) {
            orden.setEstOrd(EstadoOrden.ABIERTA);
        }

        // Usuario de sesión
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        // Guardar orden con usuario activo usando actualizarOrden
        ordenServicio.actualizarOrden(orden, orden.getCliente(), usuarioActivo);

        return ResponseEntity.ok().build();
    }
}
