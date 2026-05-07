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

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecOrdDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecOrdHasta,
            @RequestParam(required = false) Integer estOrd,
            @PageableDefault(page = 0, size = 10, sort = "fecOrd", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap modelo) {

        Page<Orden> paginaOrdenes = ordenServicio.listarOrdenesFiltradas(
                numOrd, idCliente, fecOrdDesde, fecOrdHasta, estOrd, pageable);

        // ✅ Línea completa
        modelo.put("paginaOrdenes", paginaOrdenes);

        return "ordenes"; // nombre de la vista Thymeleaf
    }

}