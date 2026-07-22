package wcsono.strgSys.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wcsono.strgSys.modelo.*;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.enums.TipoMovimiento;
import wcsono.strgSys.repositorio.ClienteRepositorio;
import wcsono.strgSys.repositorio.OrdenRepositorio;
import wcsono.strgSys.repositorio.ArticuloRepositorio;
import wcsono.strgSys.repositorio.MovimientoRepositorio;

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
    private ArticuloRepositorio articuloRepositorio;

    @Autowired
    private MovimientoRepositorio movimientoRepositorio;

    @Autowired
    private FacturacionServicio facturacionServicio; // 🔹 para anular facturas

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
    public List<Orden> listarOrdenesOrdenadasPorFechaDesc() {
        return ordenRepositorio.findAllByOrderByFecOrdDesc();
    }

    @Override
    public List<Orden> listarOrdenesOrdenadasPorIdDesc() {
        return ordenRepositorio.findAllByOrderByIdOrdDesc();
    }

    @Override
    public Orden buscarOrdenConTipoDocumentoYDetalles(Integer id) {
        return ordenRepositorio.findWithTipoDocumentoAndDetallesByIdOrd(id);
    }

    @Override
    @Transactional
    public Orden guardarOrden(Orden orden, Cliente cliente, Usuario usuario) {
        if (cliente.getIdCliente() == null) {
            cliente = clienteRepositorio.save(cliente);
        }
        orden.setCliente(cliente);
        orden.setUsuario(usuario);
        orden.setEstOrd(EstadoOrden.INICIAL);
        orden.setCosOrd(BigDecimal.ZERO);
        orden.setFechaEstado(LocalDateTime.now());
        orden = ordenRepositorio.save(orden);
        orden.setNumOrd(String.valueOf(1000 + orden.getIdOrd()));
        Orden saved = ordenRepositorio.save(orden);
        logger.info("Orden guardada -> id={}, numOrd={}, estOrd={}, fechaEstado={}, cliente={}, usuario={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado(),
                saved.getCliente().getNomCli(), saved.getUsuario().getNombre());
        return saved;
    }

    @Override
    @Transactional
    public Orden actualizarOrden(Orden orden, Cliente cliente, Usuario usuario) {
        orden.setCliente(cliente);
        orden.setUsuario(usuario);
        orden.setFechaEstado(LocalDateTime.now());
        Orden saved = ordenRepositorio.save(orden);
        logger.info("Orden actualizada -> id={}, numOrd={}, estOrd={}, fechaEstado={}, cosOrd={}, cliente={}, usuario={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado(),
                saved.getCosOrd(), saved.getCliente().getNomCli(), saved.getUsuario().getNombre());
        return saved;
    }

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
            Articulo articulo = det.getArticulo();
            int nuevoStock = esEntrada ? articulo.getStk() - det.getCantidad() : articulo.getStk() + det.getCantidad();
            articulo.setStk(nuevoStock);
            articuloRepositorio.save(articulo);
        });
        orden.setEstOrd(EstadoOrden.EXTORNADA);
        orden.setFechaEstado(LocalDateTime.now());
        ordenRepositorio.save(orden);
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
    @Transactional
    public Orden actualizarEstadoOrden(Orden orden, Usuario usuario) {
        Orden ordenExistente = ordenRepositorio.findById(orden.getIdOrd())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        ordenExistente.setEstOrd(orden.getEstOrd());
        ordenExistente.setFechaEstado(LocalDateTime.now());
        ordenExistente.setUsuario(usuario);

        // 🔹 Procesar movimientos solo en estados ENTREGADA o INGRESADA
        if (ordenExistente.getEstOrd() == EstadoOrden.ENTREGADA
                || ordenExistente.getEstOrd() == EstadoOrden.INGRESADA) {
            for (DetalleOrden detalle : ordenExistente.getDetalles()) {
                Articulo articulo = detalle.getArticulo();
                if (ordenExistente.getEstOrd() == EstadoOrden.INGRESADA) {
                    articulo.setStk(articulo.getStk() + detalle.getCantidad());
                } else if (ordenExistente.getEstOrd() == EstadoOrden.ENTREGADA) {
                    articulo.setStk(articulo.getStk() - detalle.getCantidad());
                }
                articuloRepositorio.save(articulo);

                Movimiento movimiento = Movimiento.builder()
                        .articulo(articulo)
                        .tipoDocumento(ordenExistente.getTipoDocumento())
                        .orden(ordenExistente)
                        .cantidad(detalle.getCantidad())
                        .costoUnitario(articulo.getCosto())
                        .fechaMovimiento(LocalDateTime.now())
                        .build();
                movimientoRepositorio.save(movimiento);
            }
        }

        Orden saved = ordenRepositorio.save(ordenExistente);
        logger.info("Orden actualizada de estado -> id={}, numOrd={}, nuevoEstado={}, fechaEstado={}, usuario={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd(), saved.getFechaEstado(), saved.getUsuario().getNombre());
        return saved;
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
                predicates = cb.and(predicates, cb.like(root.get("numOrd"), "%" + numOrd + "%"));
            }
            if (idCliente != null) {
                predicates = cb.and(predicates, cb.equal(root.get("cliente").get("idCliente"), idCliente));
            }
            if (fecOrdDesde != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("fecOrd"), fecOrdDesde));
            }
            if (fecOrdHasta != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("fecOrd"), fecOrdHasta));
            }
            if (estOrd != null) {
                predicates = cb.and(predicates, cb.equal(root.get("estOrd"), estOrd));
            }
            return predicates;
        };
        return ordenRepositorio.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void procesarEliminacionOAnulacion(Orden orden, Usuario usuario) {
        if (orden.getEstOrd() == EstadoOrden.INICIAL || orden.getEstOrd() == EstadoOrden.ABIERTA) {
            if (facturacionServicio.existenFacturasPorOrden(orden.getIdOrd())) {
                throw new RuntimeException("⚠️ No se puede eliminar la orden porque tiene facturación asociada.");
            }
            ordenRepositorio.delete(orden);
            logger.info("Orden eliminada físicamente -> id={}, numOrd={}", orden.getIdOrd(), orden.getNumOrd());

        } else if (orden.getEstOrd() == EstadoOrden.DEVUELTA) {
            orden.setEstOrd(EstadoOrden.ANULADA);
            orden.setFechaEstado(LocalDateTime.now());
            orden.setUsuario(usuario);
            ordenRepositorio.save(orden);

            facturacionServicio.anularFacturasPorOrden(orden.getIdOrd());
            logger.info("Orden devuelta anulada -> id={}, numOrd={}", orden.getIdOrd(), orden.getNumOrd());

        } else {
            throw new RuntimeException("⚠️ La orden no puede eliminarse ni anularse en su estado actual.");
        }
    }


    @Override
    @Transactional
    public void procesarEliminacionOAnulacion(Integer idOrd, Usuario usuarioActivo) {
        Orden orden = ordenRepositorio.findById(idOrd)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        switch (orden.getEstOrd()) {
            case DEVUELTA:
                // ✅ Nunca se elimina, siempre se anula junto con sus facturas
                orden.setEstOrd(EstadoOrden.ANULADA);
                ordenRepositorio.save(orden);
                facturacionServicio.anularFacturasPorOrden(idOrd);
                logger.info("Orden {} DEVUELTA fue ANULADA junto con sus facturas por {}", idOrd, usuarioActivo.getNombre());
                break;

            case INICIAL:
            case ABIERTA:
                if (facturacionServicio.existenFacturasPorOrden(idOrd)) {
                    // ✅ Tiene facturas → se anula
                    orden.setEstOrd(EstadoOrden.ANULADA);
                    ordenRepositorio.save(orden);
                    facturacionServicio.anularFacturasPorOrden(idOrd);
                    logger.info("Orden {} con facturas fue ANULADA junto con sus facturas por {}", idOrd, usuarioActivo.getNombre());
                } else {
                    // ✅ No tiene facturas → se elimina
                    ordenRepositorio.deleteById(idOrd);
                    logger.info("Orden {} eliminada por {}", idOrd, usuarioActivo.getNombre());
                }
                break;

            default:
                // ✅ Otros estados no se eliminan ni se anulan
                logger.warn("Orden {} en estado {} no puede eliminarse ni anularse", idOrd, orden.getEstOrd());
                throw new IllegalStateException("No se permite la anulación o eliminación de una orden en estado " + orden.getEstOrd());
        }
    }

}
