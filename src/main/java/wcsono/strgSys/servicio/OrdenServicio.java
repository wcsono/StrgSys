package wcsono.strgSys.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.enums.TipoMovimiento;
import wcsono.strgSys.repositorio.ClienteRepositorio;
import wcsono.strgSys.repositorio.OrdenRepositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenServicio implements IOrdenServicio {

    private final Logger logger = LoggerFactory.getLogger(OrdenServicio.class);

    @Autowired
    private OrdenRepositorio ordenRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ArticuloServicio articuloServicio;

    @Override
    public Page<Orden> listarOrdenesConTipoDocumento(Pageable pageable) {
        return ordenRepositorio.listarConTipoDocumento(pageable);
    }

    @Override
    public Page<Orden> listarOrdenes(Pageable pageable) {
        return ordenRepositorio.findAll(pageable);
    }

    @Override
    public Orden buscarOrdenPorId(Integer id) {
        return ordenRepositorio.findById(id).orElse(null);
    }

    @Override
    public Orden buscarOrdenConDetalles(Integer id) {
        return ordenRepositorio.findByIdOrd(id);
    }

    @Override
    public Orden buscarOrdenConTipoDocumentoYDetalles(Integer id) {
        return ordenRepositorio.findWithTipoDocumentoAndDetallesByIdOrd(id);
    }

    /**
     * Guardar una nueva orden con cliente y usuario activo.
     * Estado inicial = INICIAL, costo inicial = 0.
     */
    @Override
    @Transactional
    public Orden guardarOrden(Orden orden, Cliente cliente, Usuario usuario) {
        if (cliente.getIdCliente() == null) {
            cliente = clienteRepositorio.save(cliente);
        }
        orden.setCliente(cliente);
        orden.setUsuario(usuario);

        orden.setEstOrd(EstadoOrden.INICIAL); // Estado inicial
        orden.setCosOrd(BigDecimal.ZERO);     // Costo inicial
        orden.setFechaEstado(LocalDateTime.now()); // fecha/hora inicial

        orden = ordenRepositorio.save(orden);
        orden.setNumOrd(String.valueOf(1000 + orden.getIdOrd()));

        Orden saved = ordenRepositorio.save(orden);

        logger.info("Orden guardada -> id={}, numOrd={}, estOrd={}, fechaEstado={}, cliente={}, usuario={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado(),
                saved.getCliente().getNomCli(), saved.getUsuario().getNombre());

        return saved;
    }

    /**
     * Actualizar una orden existente (estado, costo, usuario).
     */
    @Override
    @Transactional
    public Orden actualizarOrden(Orden orden, Cliente cliente, Usuario usuario) {
        orden.setCliente(cliente);
        orden.setUsuario(usuario);

        orden.setFechaEstado(LocalDateTime.now());

        Orden saved = ordenRepositorio.save(orden);

        logger.info("Orden actualizada -> id={}, numOrd={}, estOrd={}, fechaEstado={}, cosOrd={}, cliente={}, usuario={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado(), saved.getCosOrd(),
                saved.getCliente().getNomCli(), saved.getUsuario().getNombre());

        return saved;
    }

    /**
     * ✅ Nuevo método: actualizar solo el estado y fechaEstado de la orden.
     */
    @Override
    @Transactional
    public Orden actualizarEstadoOrden(Integer idOrd, EstadoOrden nuevoEstado) {
        Orden orden = ordenRepositorio.findById(idOrd)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        orden.setEstOrd(nuevoEstado);
        orden.setFechaEstado(LocalDateTime.now());

        Orden saved = ordenRepositorio.save(orden);

        logger.info("Orden actualizada de estado -> id={}, numOrd={}, nuevoEstado={}, fechaEstado={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado());

        return saved;
    }

    @Override
    public void eliminarOrden(Orden orden) {
        ordenRepositorio.delete(orden);
        logger.info("Orden eliminada físicamente -> id={}", orden.getIdOrd());
    }

    @Override
    @Transactional
    public void extornarOrden(Integer id) {
        Orden orden = buscarOrdenConTipoDocumentoYDetalles(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        boolean esEntrada = orden.getTipoDocumento().getTipoMovimiento() == TipoMovimiento.INGRESO;

        orden.getDetalles().forEach(det -> {
            var articulo = det.getArticulo();
            int nuevoStock = esEntrada
                    ? articulo.getStk() - det.getCantidad()
                    : articulo.getStk() + det.getCantidad();
            articulo.setStk(nuevoStock);
            articuloServicio.guardarArticulo(articulo);
        });

        orden.setEstOrd(EstadoOrden.EXTORNADA);
        orden.setFechaEstado(LocalDateTime.now());

        actualizarOrden(orden, orden.getCliente(), orden.getUsuario());

        logger.info("Orden extornada -> id={}, numOrd={}, fechaEstado={}", orden.getIdOrd(), orden.getNumOrd(), orden.getFechaEstado());
    }

    @Override
    public boolean validarNumOrdUnico(String numOrd) {
        return !ordenRepositorio.existsByNumOrd(numOrd);
    }

    @Override
    public List<Orden> listarOrdenesPorEstado(EstadoOrden estOrd) {
        return ordenRepositorio.findByEstOrd(estOrd);
    }

    @Override
    public List<Orden> listarOrdenesPorEstados(List<EstadoOrden> estados) {
        return ordenRepositorio.findByEstOrdIn(estados);
    }

    @Override
    public List<Orden> listarOrdenesPorCliente(Integer idCliente) {
        return ordenRepositorio.findByCliente_IdCliente(idCliente);
    }

    @Override
    public List<Orden> listarOrdenesPorUsuario(Integer idUsuario) {
        return ordenRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Object[]> obtenerEntradasVsSalidasPorMes() {
        return ordenRepositorio.obtenerEntradasVsSalidasPorMes();
    }

    @Override
    public Page<Orden> listarOrdenesFiltradas(
            String numOrd,
            Integer idCliente,
            LocalDate fecOrdDesde,
            LocalDate fecOrdHasta,
            Integer estOrd,
            Pageable pageable) {

        Specification<Orden> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (numOrd != null && !numOrd.isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("numOrd"), "%" + numOrd + "%"));
            }

            if (idCliente != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("cliente").get("idCliente"), idCliente));
            }

            if (fecOrdDesde != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("fecOrd"), fecOrdDesde));
            }
            if (fecOrdHasta != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("fecOrd"), fecOrdHasta));
            }

            if (estOrd != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("estOrd"), estOrd));
            }

            return predicates;
        };

        return ordenRepositorio.findAll(spec, pageable);
    }
}
