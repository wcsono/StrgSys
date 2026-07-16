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
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.modelo.DetalleOrden;
import wcsono.strgSys.modelo.Articulo;
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
    public String mostrarAgregarOrden(Model model) {
        model.addAttribute("ordenForma", new Orden());
        model.addAttribute("clienteForma", new Cliente());
        model.addAttribute("tdsAgregarOrden", tipoDocumentoServicio.listarTipoDocumentos());
        logger.info("✅ Preparando formulario de nueva orden");
        return "agregarOrden";
    }

    @PostMapping("/guardarAgregarOrden")
    public String guardarAgregarOrden(@ModelAttribute("ordenForma") Orden orden,
                                      @RequestParam String codCli,
                                      HttpSession session) {

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioActivo == null) {
            return "redirect:/login";
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
    public OrdenDTO verOrd(@PathVariable Integer id, HttpSession session) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada");
        }

        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        // 🔹 Reglas de transición de estado con usuario
        if (orden.getTipoDocumento() != null) {
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

        Orden ordenActualizada = ordenServicio.buscarOrdenPorId(id);

        OrdenDTO dto = new OrdenDTO();
        dto.setIdOrd(ordenActualizada.getIdOrd());
        dto.setNumOrd(ordenActualizada.getNumOrd());
        dto.setNomOrd(ordenActualizada.getCliente().getNomCli());
        dto.setFecOrd(ordenActualizada.getFecOrd().toString());

        // 🔹 Estado y estilo
        dto.setEstOrd(ordenActualizada.getEstOrd().getDescripcion());
        dto.setCssClass(ordenActualizada.getEstOrd().getCssClass());

        dto.setCosOrd(ordenActualizada.getCosOrd());
        dto.setTipoDocumento(
                ordenActualizada.getTipoDocumento() != null ? ordenActualizada.getTipoDocumento().getDesTd() : null
        );

        // 🔹 Formatear fechaEstado
        if (ordenActualizada.getFechaEstado() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            dto.setFechaEstado(ordenActualizada.getFechaEstado().format(formatter));
        } else {
            dto.setFechaEstado(null);
        }

        // 🔹 Nuevo: incluir usuario activo en el DTO
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


    @GetMapping("/eliminarOrd/{id}")
    public String eliminarOrden(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);

        if (orden == null) {
            redirectAttributes.addFlashAttribute("error", "⚠️ La orden no existe.");
            return "redirect:/ordenes";
        }

        ordenServicio.eliminarOrden(orden);
        redirectAttributes.addFlashAttribute("mensaje", "✅ Orden eliminada correctamente.");

        return "redirect:/ordenes";
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
            return "redirect:/login";
        }

        Cliente cliente = clienteServicio.obtenerClientePorCodigo(codCli);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "Cliente no encontrado con código: " + codCli);
            return "redirect:/orden/editar/" + orden.getIdOrd();
        }

        orden.setCliente(cliente);

        ordenServicio.actualizarOrden(orden, cliente, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje", "Orden actualizada correctamente");
        return "redirect:/ordenDetalle/" + orden.getIdOrd();
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

        try {
            String tipoMovimiento = orden.getTipoDocumento().getTipoMovimiento().name();

            if ("SALIDA".equalsIgnoreCase(tipoMovimiento)) {
                // Primera pasada: validación
                for (DetalleOrden detalle : orden.getDetalles()) {
                    Articulo articulo = detalle.getArticulo();
                    if (detalle.getCantidad() > articulo.getStk()) {
                        // ⚠️ No cambiamos estado aquí, solo enviamos flag al frontend
                        redirectAttrs.addFlashAttribute("errorStock",
                                "Stock insuficiente para el artículo: " + articulo.getDesArt());
                        return "redirect:/orden/editar/" + idOrd;
                    }
                }
                // Segunda pasada: actualización
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

            // Estado según acción
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

        // 🔹 Cambiar estado a DEVUELTA
        orden.setEstOrd(EstadoOrden.DEVUELTA);
        ordenServicio.actualizarEstadoOrden(orden, usuarioActivo);

        redirectAttrs.addFlashAttribute("mensaje", "La orden fue devuelta a Ventas para corrección.");
        return "redirect:/ordenes";
    }




} // ✅ cierre de la clase OrdenesControlador

