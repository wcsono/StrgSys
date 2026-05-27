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
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.ClienteServicio;
import wcsono.strgSys.servicio.IOrdenServicio;
import wcsono.strgSys.servicio.ITipoDocumentoServicio;
import wcsono.strgSys.servicio.MovimientoServicio;
import wcsono.strgSys.servicio.UsuarioServicio;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    private final Logger logger = LoggerFactory.getLogger(OrdenesControlador.class);

    /**
     * Listar todas las órdenes (vista Thymeleaf)
     */
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

        Page<Orden> paginaOrdenes = ordenServicio
                .listarOrdenesFiltradas(numOrd, idCliente, fecOrdDesde, fecOrdHasta, estado, pageable);

        model.addAttribute("paginaOrdenes", paginaOrdenes);
        model.addAttribute("listadoOrdenes", paginaOrdenes.getContent());

        return "ordenes";
    }

    /**
     * Mostrar formulario para agregar una nueva orden
     */
    @GetMapping("/agregarOrden")
    public String mostrarAgregarOrden(Model model) {
        model.addAttribute("ordenForma", new Orden());
        model.addAttribute("clienteForma", new Cliente());
        model.addAttribute("tdsAgregarOrden", tipoDocumentoServicio.listarTipoDocumentos());
        logger.info("✅ Preparando formulario de nueva orden");
        return "agregarOrden";
    }

    /**
     * Guardar nueva orden y redirigir a detalle
     */
    @PostMapping("/guardarAgregarOrden")
    public String guardarAgregarOrden(@ModelAttribute("orden") Orden orden,
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

        return "redirect:/orden/" + nuevaOrden.getIdOrd();
    }

    /**
     * Mostrar detalle de la orden (vista Thymeleaf)
     */
    @GetMapping("/ordenDetalle/{id}")
    public String mostrarDetalleOrden(@PathVariable Integer id, Model model) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        model.addAttribute("orden", orden);
        model.addAttribute("detalleOrdenes", orden.getDetalles());
        model.addAttribute("totalOrden", BigDecimal.ZERO);

        return "ordenDetalle";
    }

    /**
     * Endpoint REST para devolver OrdenDTO en JSON
     */
    @GetMapping("/verOrd/{id}")
    @ResponseBody
    public OrdenDTO verOrden(@PathVariable Integer id) {
        Orden orden = ordenServicio.buscarOrdenPorId(id);
        if (orden == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada");
        }

        OrdenDTO dto = new OrdenDTO();
        dto.setIdOrd(orden.getIdOrd());
        dto.setNumOrd(orden.getNumOrd());
        dto.setNomOrd(orden.getCliente().getNomCli());
        dto.setFecOrd(orden.getFecOrd().toString());
        dto.setEstOrd(orden.getEstOrd());
        dto.setCosOrd(orden.getCosOrd());
        dto.setTipoDocumento(
                orden.getTipoDocumento() != null ? orden.getTipoDocumento().getDesTd() : null
        );

        List<DetalleDTO> detalles = orden.getDetalles().stream().map(det -> {
            DetalleDTO d = new DetalleDTO();
            d.setArticulo(det.getArticulo().getDesArt());
            d.setCantidad(det.getCantidad());
            d.setCosArt(det.getCosArt());
            d.setSubtotal(det.getSubtotal());
            return d;
        }).toList();

        dto.setDetalles(detalles);

        // Log del DTO construido
        logger.info("📦 OrdenDTO construido para enviar al frontend: {}", dto);

        return dto;
    }
}
