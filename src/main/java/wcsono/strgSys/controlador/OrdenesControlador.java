package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import wcsono.strgSys.dto.DetalleDTO;
import wcsono.strgSys.dto.OrdenDTO;
import wcsono.strgSys.modelo.*;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.enums.TipoMovimiento;
import wcsono.strgSys.servicio.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


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

    @Autowired
    private DetalleOrdenServicio detalleOrdenServicio;

    private final Logger logger = LoggerFactory.getLogger(OrdenesControlador.class);

    @GetMapping("/ordenes")
    public String mostrarOrdenes(
            @RequestParam(required = false) String numOrd,
            @RequestParam(required = false) Integer idCliente,
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

        // 🔹 Forzar orden descendente por ID
        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "idOrd")
        );

        Page<Orden> paginaOrdenes = ordenServicio
                .listarOrdenesFiltradas(numOrd, idCliente, fecOrdDesde, fecOrdHasta, estado, pageableOrdenado);

        model.addAttribute("paginaOrdenes", paginaOrdenes);
        model.addAttribute("listadoOrdenes", paginaOrdenes.getContent());

        return "ordenes";
    }

    @GetMapping("/agregarOrden")
    public String mostrarAgregarOrden(Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // Restricción: solo Administrador (1) y Operador/Ventas (2)
        if (usuarioActivo.getNivelAcceso() != 1 && usuarioActivo.getNivelAcceso() != 2) {
            redirectAttrs.addFlashAttribute("error", "No tiene Permiso para realizar este proceso");
            return "redirect:/ordenes";
        }

        model.addAttribute("ordenForma", new Orden());
        model.addAttribute("clienteForma", new Cliente());
        model.addAttribute("tdsAgregarOrden", tipoDocumentoServicio.listarTipoDocumentos());
        logger.info("✅ Preparando formulario de nueva orden");

        return "agregarOrden";
    }

    @PostMapping("/guardarAgregarOrden")
    public String guardarAgregarOrden(@ModelAttribute("ordenForma") Orden orden,
                                      @RequestParam String codCli,
                                      HttpSession session,
                                      RedirectAttributes redirectAttrs) {

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // Restricción: solo Administrador (1) y Operador/Ventas (2)
        if (usuarioActivo.getNivelAcceso() != 1 && usuarioActivo.getNivelAcceso() != 2) {
            redirectAttrs.addFlashAttribute("error", "No tiene Permiso para realizar este proceso");
            return "redirect:/ordenes";
        }

        Cliente clienteExistente = clienteServicio.obtenerClientePorCodigo(codCli);
        if (clienteExistente == null) {
            return "redirect:/clientes/nuevo?codCli=" + codCli;
        }

        orden.setCliente(clienteExistente);

        if (orden.getNumOrd() == null || orden.getNumOrd().isEmpty()) {
            orden.setNumOrd("0");
        }

        Orden nuevaOrden = ordenServicio.guardarOrden(orden, clienteExistente, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje", "✅ Orden creada correctamente.");
        return "redirect:/ordenDetalle/" + nuevaOrden.getIdOrd();
    }

    @GetMapping("/ordenDetalle/{id}")
    public String mostrarDetalleOrden(@PathVariable Integer id, Model model) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        model.addAttribute("orden", orden);
        model.addAttribute("detalleOrdenes", orden.getDetalles());

        BigDecimal totalOrden = orden.getDetalles().stream()
                .map(det -> det.getSubtotal() != null ? det.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalOrden", totalOrden);
        model.addAttribute("articulos", articuloServicio.listarArticulos());

        return "ordenDetalle";
    }

    @GetMapping("/verOrd/{id}")
    @ResponseBody
    public OrdenDTO verOrd(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debe iniciar sesión para continuar.");
        }

        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada");
        }

        // 🔹 Solo Administrador (1) o Almacén (3) pueden provocar el cambio automático a EN PREPARACIÓN
        if (orden.getTipoDocumento() != null) {
            if (usuarioActivo.getNivelAcceso() == 1 || usuarioActivo.getNivelAcceso() == 3) {
                if (orden.getTipoDocumento().getTipoMovimiento() == TipoMovimiento.INGRESO
                        && orden.getEstOrd() == EstadoOrden.ABIERTA) {
                    orden.setEstOrd(EstadoOrden.PREPARACION);
                    ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);
                } else if (orden.getTipoDocumento().getTipoMovimiento() == TipoMovimiento.SALIDA
                        && orden.getEstOrd() == EstadoOrden.FACTURADA) {
                    orden.setEstOrd(EstadoOrden.PREPARACION);
                    ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);
                }
            }
        }

        Orden ordenActualizada = ordenServicio.buscarOrdenPorId(id);

        OrdenDTO dto = new OrdenDTO();
        dto.setIdOrd(ordenActualizada.getIdOrd());
        dto.setNumOrd(ordenActualizada.getNumOrd());
        dto.setNomOrd(ordenActualizada.getCliente().getNomCli());
        dto.setFecOrd(ordenActualizada.getFecOrd().toString());

        dto.setEstOrd(ordenActualizada.getEstOrd().getDescripcion());
        dto.setCssClass(ordenActualizada.getEstOrd().getCssClass());

        dto.setCosOrd(ordenActualizada.getCosOrd());
        dto.setTipoDocumento(
                ordenActualizada.getTipoDocumento() != null ? ordenActualizada.getTipoDocumento().getDesTd() : null
        );

        if (ordenActualizada.getFechaEstado() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            dto.setFechaEstado(ordenActualizada.getFechaEstado().format(formatter));
        } else {
            dto.setFechaEstado(null);
        }

        dto.setUsuarioAccion(
                ordenActualizada.getUsuario() != null ? ordenActualizada.getUsuario().getNombre() : "—"
        );

        dto.setDetalles(ordenActualizada.getDetalles().stream().map(det -> {
            DetalleDTO d = new DetalleDTO();
            d.setCodArt(det.getArticulo().getCodArt());
            d.setArticulo(det.getArticulo().getDesArt());
            d.setUbicacion(det.getArticulo().getUbiArt());
            d.setCantidad(det.getCantidad());
            d.setCosto(det.getArticulo().getCosto());
            d.setPrecioVenta(det.getArticulo().getPrecioVenta());
            d.setSubtotal(det.getSubtotal());
            return d;
        }).toList());

        return dto;
    }

    @GetMapping("/orden/editar/{idOrd}")
    public String editarOrden(@PathVariable Integer idOrd, Model model) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        List<DetalleOrden> detalles = detalleOrdenServicio.listarPorOrden(idOrd);
        List<TipoDocumento> tds = tipoDocumentoServicio.listarTipoDocumentos();
        List<Articulo> articulos = articuloServicio.listarArticulos();

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("tds", tds);
        model.addAttribute("articulos", articulos);

        return "editarOrd";
    }

    @PostMapping("/orden/guardarEdicion")
    public String guardarEdicion(@ModelAttribute Orden orden,
                                 @RequestParam String codCli,
                                 HttpSession session,
                                 RedirectAttributes redirectAttrs) {

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // 🔹 Restricción: solo Administrador (1) y Ventas (2) pueden guardar ediciones
        if (usuarioActivo.getNivelAcceso() != 1 && usuarioActivo.getNivelAcceso() != 2) {
            redirectAttrs.addFlashAttribute("error", "No tiene permisos para editar órdenes.");
            return "redirect:/ordenes";
        }

        Cliente cliente = clienteServicio.obtenerClientePorCodigo(codCli);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "Cliente no encontrado con código: " + codCli);
            return "redirect:/orden/editar/" + orden.getIdOrd();
        }

        orden.setCliente(cliente);

        ordenServicio.actualizarOrden(orden, cliente, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje", "✅ Orden actualizada correctamente.");

        // 🔹 Aquí está el cambio: en lugar de ir a la vista detalle, volvemos al formulario de edición
        return "redirect:/orden/editar/" + orden.getIdOrd();
    }

    @GetMapping("/orden/buscarCliente")
    @ResponseBody
    public Cliente buscarCliente(@RequestParam String codCli) {
        return clienteServicio.obtenerClientePorCodigo(codCli);
    }


    @PostMapping("/orden/entregadoIngresado/{idOrd}")
    public String entregadoIngresado(@PathVariable Integer idOrd,
                                     @RequestParam String accion,
                                     HttpSession session,
                                     RedirectAttributes redirectAttrs) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            redirectAttrs.addFlashAttribute("error", "Orden no encontrada");
            return "redirect:/ordenes";
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // 🔹 Restricción: solo Administrador (1) y Almacén (3)
        if (usuarioActivo.getNivelAcceso() != 1 && usuarioActivo.getNivelAcceso() != 3) {
            redirectAttrs.addFlashAttribute("error", "\"No tiene permisos para realizar este proceso");
            return "redirect:/ordenes";
        }

        try {
            String tipoMovimiento = orden.getTipoDocumento().getTipoMovimiento().name();

            if ("SALIDA".equalsIgnoreCase(tipoMovimiento)) {
                for (DetalleOrden detalle : orden.getDetalles()) {
                    Articulo articulo = detalle.getArticulo();
                    if (detalle.getCantidad() > articulo.getStk()) {
                        redirectAttrs.addFlashAttribute("errorStock",
                                "Stock insuficiente para el artículo: " + articulo.getDesArt());
                        return "redirect:/orden/editar/" + idOrd;
                    }
                }
                for (DetalleOrden detalle : orden.getDetalles()) {
                    Articulo articulo = detalle.getArticulo();
                    articulo.setStk(articulo.getStk() - detalle.getCantidad());
                    articuloServicio.guardarArticulo(articulo);
                }
            } else if ("INGRESO".equalsIgnoreCase(tipoMovimiento)) {
                for (DetalleOrden detalle : orden.getDetalles()) {
                    Articulo articulo = detalle.getArticulo();
                    articulo.setStk(articulo.getStk() + detalle.getCantidad());
                    articuloServicio.guardarArticulo(articulo);
                }
            }

            if ("ENTREGAR".equalsIgnoreCase(accion)) {
                orden.setEstOrd(EstadoOrden.ENTREGADA);
            } else if ("INGRESAR".equalsIgnoreCase(accion)) {
                orden.setEstOrd(EstadoOrden.INGRESADA);
            } else {
                redirectAttrs.addFlashAttribute("error", "Acción inválida: " + accion);
                return "redirect:/ordenDetalle/" + idOrd;
            }

            ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);
            redirectAttrs.addFlashAttribute("mensaje",
                    "✅ Orden actualizada a estado: " + orden.getEstOrd().getDescripcion());

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
        }

        return "redirect:/ordenes";
    }

    @PostMapping("/orden/devolver/{idOrd}")
    public String devolverOrden(@PathVariable Integer idOrd,
                                HttpSession session,
                                RedirectAttributes redirectAttrs) {
        Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
        if (orden == null) {
            redirectAttrs.addFlashAttribute("error", "Orden no encontrada");
            return "redirect:/ordenes";
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // 🔹 Restricción: solo Administrador (1) y Almacén (3)
        if (usuarioActivo.getNivelAcceso() != 1 && usuarioActivo.getNivelAcceso() != 3) {
            redirectAttrs.addFlashAttribute("error", "No tiene permisos para realizar este proceso");
            return "redirect:/ordenes";
        }

        try {
            orden.setEstOrd(EstadoOrden.DEVUELTA);
            ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);
            redirectAttrs.addFlashAttribute("mensaje", "✅ Orden marcada como DEVUELTA correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al devolver la orden: " + e.getMessage());
        }

        return "redirect:/ordenes";
    }


    @PostMapping("/orden/{id}/eliminarOAnular")
    public String eliminarOAnular(@PathVariable Integer id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        // 🔹 Validar sesión
        if (usuarioActivo == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
            return "redirect:/login";
        }

        // 🔹 Bloquear Almacén (nivel 3)
        if (usuarioActivo.getNivelAcceso() != null && usuarioActivo.getNivelAcceso() == 3) {
            redirectAttributes.addFlashAttribute("error", "No tiene Permiso para realizar este proceso");
            return "redirect:/ordenes";
        }

        // 🔹 Procesar eliminación/anulación
        try {
            ordenServicio.procesarEliminacionOAnulacion(id, usuarioActivo);
            redirectAttributes.addFlashAttribute("mensaje", "✅ Proceso de eliminación/anulación ejecutado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ordenes";
    }


    // Proceso de cerrar una Orden
@GetMapping("/orden/{id}/cerrar")
public String cerrarOrden(@PathVariable("id") Integer idOrd,
                          HttpSession session,
                          RedirectAttributes redirectAttrs) {
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
    if (usuarioActivo == null) {
        redirectAttrs.addFlashAttribute("error", "Debe iniciar sesión para continuar.");
        return "redirect:/login";
    }

    // Restricción: solo nivelAcceso = 1 puede cerrar órdenes
    if (usuarioActivo.getNivelAcceso() != 1) {
        redirectAttrs.addFlashAttribute("error", "No tiene permisos para cerrar órdenes.");
        return "redirect:/ordenes";
    }

    try {
        Orden orden = new Orden();
        orden.setIdOrd(idOrd);
        orden.setEstOrd(EstadoOrden.CERRADA);

        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje", "La orden fue cerrada correctamente.");
    } catch (RuntimeException e) {
        redirectAttrs.addFlashAttribute("error", "Error al cerrar la orden: " + e.getMessage());
    }


    return "redirect:/ordenes";
}

//EndPoint de Extornar una Orden
@GetMapping("/orden/{idOrd}/extornar")
public String extornarOrden(@PathVariable Integer idOrd,
                            HttpSession session,
                            RedirectAttributes redirectAttrs) {
    Orden orden = ordenServicio.buscarOrdenPorId(idOrd);
    if (orden == null) {
        redirectAttrs.addFlashAttribute("error", "⚠️ Orden no encontrada.");
        return "redirect:/ordenes";
    }

    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
    if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) {
        redirectAttrs.addFlashAttribute("error", "❌ Solo un Administrador puede extornar órdenes.");
        return "redirect:/ordenes";
    }

    // Validar estados permitidos
    if (!(orden.getEstOrd() == EstadoOrden.ENTREGADA
            || orden.getEstOrd() == EstadoOrden.INGRESADA
            || orden.getEstOrd() == EstadoOrden.CERRADA)) {
        redirectAttrs.addFlashAttribute("error",
                "❌ No se puede extornar una orden en estado " + orden.getEstOrd().getDescripcion());
        return "redirect:/ordenes";
    }

    try {
        // Invertir movimientos de inventario y registrar en Movimientos
        for (DetalleOrden detalle : orden.getDetalles()) {
            Articulo articulo = detalle.getArticulo();
            Integer cantidad = detalle.getCantidad();

            if (orden.getTipoDocumento().getTipoMovimiento() == TipoMovimiento.INGRESO) {
                // Si fue ingreso, ahora restamos
                articulo.setStk(articulo.getStk() - cantidad);
            } else if (orden.getTipoDocumento().getTipoMovimiento() == TipoMovimiento.SALIDA) {
                // Si fue salida, ahora sumamos
                articulo.setStk(articulo.getStk() + cantidad);
            }
            articuloServicio.guardarArticulo(articulo);

            // 🔹 Registrar movimiento de extorno
            Movimiento mov = new Movimiento();
            mov.setArticulo(articulo);
            mov.setTipoDocumento(orden.getTipoDocumento());
            mov.setOrden(orden);
            mov.setCantidad(cantidad);
            mov.setCostoUnitario(detalle.getCosArt() != null ? detalle.getCosArt() : detalle.getPrecioVenta());
            mov.setFechaMovimiento(LocalDateTime.now());

            // El valorMovimiento se calcula automáticamente en @PrePersist/@PreUpdate
            movimientoServicio.guardarMovimiento(mov);
        }

        // Cambiar estado a EXTORNADA con fecha y usuario
        orden.setEstOrd(EstadoOrden.EXTORNADA);
        orden.setFechaEstado(LocalDateTime.now());
        orden.setUsuario(usuarioActivo);

        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje",
                "✅ La orden " + idOrd + " fue extornada correctamente. Nuevo estado: EXTORNADA");

    } catch (Exception e) {
        redirectAttrs.addFlashAttribute("error", "Error al extornar la orden: " + e.getMessage());
    }

    return "redirect:/ordenes";
}

} // ✅ cierre de la clase OrdenesControlador

