package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Articulo;
import wcsono.strgSys.modelo.DetalleOrden;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.OrdenServicio;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.DetalleOrdenServicio;
import wcsono.strgSys.servicio.UsuarioServicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orden")
public class OrdenDetalleControlador {

    @Autowired
    private OrdenServicio ordenServicio;

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private DetalleOrdenServicio detalleOrdenServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    /**
     * Mostrar detalle de la orden
     */
    @GetMapping("/{id}")
    public String verDetalleOrden(@PathVariable Integer id, Model model) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);

        // Obtener detalles de la orden
        List<DetalleOrden> detalleOrdenes = orden.getDetalles();

        // Calcular el total de la orden
        BigDecimal totalOrden = detalleOrdenes.stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Pasar datos al modelo
        model.addAttribute("orden", orden);
        model.addAttribute("detalleOrdenes", detalleOrdenes);
        model.addAttribute("articulos", articuloServicio.listarArticulos());
        model.addAttribute("totalOrden", totalOrden);

        return "ordenDetalle"; // tu plantilla Thymeleaf
    }

    /**
     * Agregar artículo a la orden
     */
    @PostMapping("/{id}/articulos")
    public String agregarArticulo(@PathVariable Integer id,
                                  @RequestParam Integer idArt,
                                  @RequestParam Integer cantidad,
                                  @RequestParam BigDecimal precio,
                                  Principal principal) {

        Orden orden = ordenServicio.buscarOrdenPorId(id);
        Articulo articulo = articuloServicio.buscarArticuloPorId(idArt);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setArticulo(articulo);
        detalle.setCantidad(cantidad);
        detalle.setCosArt(precio);

        // subtotal se calcula automáticamente en la entidad con @PrePersist/@PreUpdate
        detalleOrdenServicio.guardar(detalle);

        // calcular subtotal del detalle
        BigDecimal nuevoSubtotal = precio
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        // actualizar costo total de la orden
        if (orden.getCosOrd() == null) {
            orden.setCosOrd(nuevoSubtotal);
        } else {
            orden.setCosOrd(orden.getCosOrd().add(nuevoSubtotal));
        }

        // obtener usuario activo con el método correcto
        Usuario usuarioActivo = usuarioServicio.obtenerUsuarioPorUser(principal.getName());

        // obtener cliente asociado a la orden
        Cliente cliente = orden.getCliente();

        // guardar orden con cliente y usuario
        ordenServicio.guardarOrden(orden, cliente, usuarioActivo);

        return "redirect:/orden/" + id;
    }
}
