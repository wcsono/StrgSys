package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.IOrdenServicio;
import wcsono.strgSys.servicio.ITipoDocumentoServicio;
import wcsono.strgSys.servicio.MovimientoServicio;
import wcsono.strgSys.modelo.Movimiento;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Controller
public class OrdenesControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private IOrdenServicio ordenServicio;

    @Autowired
    private ITipoDocumentoServicio tipoDocumentoServicio;

    @Autowired
    MovimientoServicio movimientoServicio;


    private final Logger logger = LoggerFactory.getLogger(OrdenesControlador.class);

    /**
     * Listar todas las órdenes (vista Thymeleaf)
     */
    @GetMapping("/ordenes")
    public String mostrarOrdenes(
            @RequestParam(required = false) String numOrd,
            @RequestParam(required = false) String nomOrd,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecOrdDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecOrdHasta,
            @RequestParam(required = false) Boolean estOrd,
            @PageableDefault(page = 0, size = 10, sort = "fecOrd", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap modelo) {

        // Log request pageable y filtros para depuración
        logger.info("Request /ordenes -> page={}, size={}, sort={}, numOrd={}, nomOrd={}, fecOrdDesde={}, fecOrdHasta={}, estOrd={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort(),
                numOrd, nomOrd, fecOrdDesde, fecOrdHasta, estOrd);

        Page<Orden> paginaOrdenes = ordenServicio.listarOrdenesFiltradas(
                numOrd, nomOrd, fecOrdDesde, fecOrdHasta, estOrd, pageable);

        // Logs del resultado
        logger.info("Page result -> totalElements={}, totalPages={}, number={}, size={}",
                paginaOrdenes.getTotalElements(),
                paginaOrdenes.getTotalPages(),
                paginaOrdenes.getNumber(),
                paginaOrdenes.getSize());

        // Atributos para la vista
        modelo.put("paginaOrdenes", paginaOrdenes);
        modelo.put("listadoOrdenes", paginaOrdenes.getContent());

        // Mantener valores de filtros en el modelo
        modelo.put("numOrd", numOrd);
        modelo.put("nomOrd", nomOrd);
        modelo.put("fecOrdDesde", fecOrdDesde);
        modelo.put("fecOrdHasta", fecOrdHasta);
        modelo.put("estOrd", estOrd);

        return "ordenes";
    }
    /**
     * Ver una orden en formato JSON (API REST)
     */
    @GetMapping("/verOrd/{id}")
    @ResponseBody
    public ResponseEntity<Orden> verOrden(@PathVariable Integer id) {
        Orden orden = ordenServicio.buscarOrdenConDetalles(id);
        if (orden == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orden);
    }

    /**
     * Ver detalle de orden en fragmento Thymeleaf
     */
    @GetMapping("/detalleOrd/{id}")
    public String verDetalleOrden(@PathVariable Integer id, ModelMap modelo) {
        Orden orden = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        modelo.put("orden", orden);
        modelo.put("detalles", orden.getDetalles());
        modelo.put("articulos", articuloServicio.listarArticulos()); // 🔹 agregar esta línea

        return "fragmentos/detalle-orden :: detalle"; // se mantiene igual
    }
    /**
     * Mostrar formulario para agregar una nueva orden
     */
    @GetMapping("/agregarOrden")
    public String mostrarAgregarOrden(ModelMap modelo) {
        List<TipoDocumento> tdocs = tipoDocumentoServicio.listarTipoDocumentos();
        modelo.put("tdsAgregarOrden", tdocs);

        // Inicializar el objeto ordenForma con un TipoDocumento vacío
        Orden orden = new Orden();
        orden.setTipoDocumento(new TipoDocumento());
        modelo.put("ordenForma", orden);

        return "agregarOrden";
    }

    /**
     * Guardar nueva orden
     */
    @PostMapping("/guardarAgregarOrden")
    public String guardarOrden(@ModelAttribute("ordenForma") Orden orden) {
        // Validar que se haya seleccionado un TipoDocumento
        if (orden.getTipoDocumento() != null && orden.getTipoDocumento().getIdTd() != null) {
            // Recuperar el TipoDocumento completo desde la BD
            TipoDocumento td = tipoDocumentoServicio.buscarTdPorId(orden.getTipoDocumento().getIdTd());
            orden.setTipoDocumento(td);
        } else {
            throw new IllegalArgumentException("Debe seleccionar un Tipo de Documento válido");
        }

        // Guardar la orden con el TipoDocumento ya cargado
        Orden ordenGuardada = ordenServicio.guardarOrden(orden);
        return "redirect:/orden/" + ordenGuardada.getIdOrd();
    }

    /**
     * Eliminar orden
     */
    @GetMapping("/eliminarOrd/{id}")
    public String eliminarOrden(@PathVariable("id") int idOrd,
                                RedirectAttributes redirectAttrs) {
        Orden ordenEliminar = ordenServicio.buscarOrdenPorId(idOrd);

        if (ordenEliminar != null) {
            if (!ordenEliminar.isEstOrd()) {
                ordenServicio.eliminarOrden(ordenEliminar);
                redirectAttrs.addFlashAttribute("mensaje", "Orden eliminada correctamente.");
            } else {
                redirectAttrs.addFlashAttribute("error", "Orden con Articulos no se puede eliminar. Proceda con el Extorno desde Editar orden.");
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "La Orden no existe.");
        }

        return "redirect:/ordenes";
    }

    @PostMapping("/orden/{id}/cerrar")
    public String cerrarOrden(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        Orden orden = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        // Calcular el costo total sumando los artículos con BigDecimal
        BigDecimal total = orden.getDetalles().stream()
                .map(det -> det.getCosArt().multiply(BigDecimal.valueOf(det.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Actualizar campos de la orden
        orden.setCosOrd(total);
        orden.setEstOrd(true); // true = cerrada

        // Lógica de stock según tipTd
        boolean esEntrada = orden.getTipoDocumento().isTipTd(); // true = suma, false = resta

        // Procesar cada detalle de la orden
        orden.getDetalles().forEach(det -> {
            var articulo = det.getArticulo();

            // Stock: suma o resta según tipTd
            int nuevoStock = esEntrada
                    ? articulo.getStk() + det.getCantidad()
                    : articulo.getStk() - det.getCantidad();
            articulo.setStk(nuevoStock);

            // Actualizar costo con el precio unitario registrado
            articulo.setCosto(det.getCosArt());

            // estArt siempre true al estar vinculado a una orden
            articulo.setEstArt(true);

            // Guardar cambios en artículo
            articuloServicio.guardarArticulo(articulo);

            logger.info("Artículo {} actualizado: stock={}, costo={}, estArt={}",
                    articulo.getCodArt(), articulo.getStk(), articulo.getCosto(), articulo.isEstArt());

            // 🔹 Registrar movimiento automáticamente con fecha de la orden
            movimientoServicio.guardarMovimiento(
                    Movimiento.builder()
                            .orden(orden)
                            .articulo(articulo)
                            .tipoDocumento(orden.getTipoDocumento())
                            .cantidad(det.getCantidad())
                            .costoUnitario(det.getCosArt())
                            .fechaMovimiento(orden.getFecOrd().atStartOfDay()) // usamos fecha de la orden
                            .build()
            );
        });

        // Guardar la orden cerrada
        ordenServicio.guardarOrden(orden);

        redirectAttrs.addFlashAttribute("mensaje", "Orden cerrada exitosamente, artículos actualizados y movimientos registrados.");
        return "redirect:/ordenes";
    }

    /**
     * Mostrar formulario de edición de una orden
     */
    @GetMapping("/editarOrd/{id}")
    public String mostrarEdicionOrden(@PathVariable("id") Integer idOrd, ModelMap modelo) {
        Orden orden = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(idOrd);

        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        modelo.put("orden", orden);
        modelo.put("detalles", orden.getDetalles());
        modelo.put("articulos", articuloServicio.listarArticulos()); // para el offcanvas
        modelo.put("tds", tipoDocumentoServicio.listarTipoDocumentos()); // 🔹 agregar lista de TipoDocumento

        return "editarOrd";
    }

    //    procedimiento para guardar los cambion en la orden
    @PostMapping("/orden/guardarEdicion")
    public String guardarEdicionOrden(@ModelAttribute Orden orden,
                                      RedirectAttributes redirectAttrs) {
        // Buscar la orden existente
        Orden ordenExistente = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(orden.getIdOrd());
        if (ordenExistente == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        // Actualizar campos editables
        ordenExistente.setNumOrd(orden.getNumOrd());
        ordenExistente.setNomOrd(orden.getNomOrd());
        ordenExistente.setFecOrd(orden.getFecOrd());
        ordenExistente.setNdocRef(orden.getNdocRef());

        // Recuperar el TipoDocumento completo desde la BD
        if (orden.getTipoDocumento() != null && orden.getTipoDocumento().getIdTd() != null) {
            TipoDocumento td = tipoDocumentoServicio.buscarTdPorId(orden.getTipoDocumento().getIdTd());
            if (td == null) {
                throw new IllegalArgumentException("Tipo de Documento inválido");
            }
            ordenExistente.setTipoDocumento(td);
        }

        // Guardar cambios
        ordenServicio.guardarOrden(ordenExistente);

// 🔹 Recargar la orden para asegurar que el TipoDocumento está actualizado
        Orden ordenRefrescada = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(ordenExistente.getIdOrd());

        redirectAttrs.addFlashAttribute("mensaje", "Orden actualizada correctamente.");
        return "redirect:/editarOrd/" + ordenRefrescada.getIdOrd();
    }

//    Extorno de Ordenes
@PostMapping("/orden/{id}/extornar")
public String extornarOrden(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
    Orden orden = ordenServicio.buscarOrdenConTipoDocumentoYDetalles(id);
    if (orden == null) {
        throw new IllegalArgumentException("Orden no encontrada");
    }

    // Lógica de stock inversa según tipTd
    boolean esEntrada = orden.getTipoDocumento().isTipTd(); // true = suma, false = resta

    orden.getDetalles().forEach(det -> {
        var articulo = det.getArticulo();

        // 🔹 Invertimos la lógica: si era entrada, ahora restamos; si era salida, ahora sumamos
        int nuevoStock = esEntrada
                ? articulo.getStk() - det.getCantidad()
                : articulo.getStk() + det.getCantidad();
        articulo.setStk(nuevoStock);

        // El costo se mantiene igual al registrado en el detalle
        articulo.setCosto(det.getCosArt());
        articulo.setEstArt(true);

        // Guardar cambios en artículo
        articuloServicio.guardarArticulo(articulo);

        logger.info("Artículo {} extornado: stock={}, costo={}, estArt={}",
                articulo.getCodArt(), articulo.getStk(), articulo.getCosto(), articulo.isEstArt());

        // 🔹 Registrar movimiento de extorno con fecha actual
        movimientoServicio.guardarMovimiento(
                Movimiento.builder()
                        .orden(orden)
                        .articulo(articulo)
                        .tipoDocumento(orden.getTipoDocumento()) // se mantiene el mismo tipoDocumento
                        .cantidad(det.getCantidad())
                        .costoUnitario(det.getCosArt())
                        .fechaMovimiento(LocalDateTime.now()) // usamos fecha actual para extorno
                        .build()
        );
    });

    // Guardar la orden extornada (puedes marcarla como abierta o con estado especial)
    orden.setEstOrd(false);
    ordenServicio.guardarOrden(orden);

    redirectAttrs.addFlashAttribute("mensaje", "Orden extornada exitosamente, stock revertido y movimientos registrados.");
    return "redirect:/ordenes";
}

//Metodo que carga los Articulos en el fragmento offcanvas-articulos
@GetMapping("/fragmento/articulos")
public String cargarFragmentoArticulos(ModelMap modelo) {
    modelo.put("articulos", articuloServicio.listarArticulos());
    return "fragmentos/offcanvas-articulos :: offcanvas-articulos-seccion";
}

@GetMapping("/validarNumOrd")
@ResponseBody
public boolean validarNumOrd(@RequestParam String numOrd) {
return ordenServicio.validarNumOrdUnico(numOrd);
}

//ya se realizo la migracion de la bd
}