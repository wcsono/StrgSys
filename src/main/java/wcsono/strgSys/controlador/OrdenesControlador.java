package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.IOrdenServicio;
import wcsono.strgSys.servicio.ITipoDocumentoServicio;
import wcsono.strgSys.servicio.MovimientoServicio;

import java.time.LocalDate;

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

        modelo.put("paginaOrdenes", paginaOrdenes);

        return "ordenes"; // nombre de la vista Thymeleaf
    }

    /**
     * Mostrar formulario para agregar una nueva orden
     */
    @GetMapping("/agregarOrden")
    public String mostrarAgregarOrden(Model model) {
        model.addAttribute("ordenForma", new Orden());
        model.addAttribute("clienteForma", new Cliente()); // 🔹 necesario para el fragmento del modal
        model.addAttribute("tdsAgregarOrden", tipoDocumentoServicio.listarTipoDocumentos()); // lista de tipos de documento
        logger.info("✅ Preparando formulario de nueva orden con objetos ordenForma y clienteForma");
        return "agregarOrden";
    }
}
