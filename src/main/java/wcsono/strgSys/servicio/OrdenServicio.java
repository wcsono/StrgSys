package wcsono.strgSys.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.repositorio.OrdenRepositorio;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdenServicio implements IOrdenServicio {

    private final Logger logger = LoggerFactory.getLogger(OrdenServicio.class);

    @Autowired
    private OrdenRepositorio ordenRepositorio;

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

    @Override
    @Transactional
    public Orden guardarOrden(Orden orden) {
        Orden saved = ordenRepositorio.save(orden);

        // Generar numOrd automáticamente si no existe
        if (saved.getNumOrd() == null || saved.getNumOrd().isEmpty()) {
            saved.setNumOrd(String.valueOf(1000 + saved.getIdOrd()));
            saved = ordenRepositorio.save(saved);
        }

        logger.info("Orden guardada -> id={}, numOrd={}, estOrd={}",
                saved.getIdOrd(), saved.getNumOrd(), saved.getEstOrd());
        return saved;
    }

    @Override
    public void eliminarOrden(Orden orden) {
        if (orden.getCliente() != null || orden.getUsuario() != null) {
            throw new IllegalStateException("No se puede eliminar una orden vinculada a Cliente/Usuario");
        }
        ordenRepositorio.delete(orden);
        logger.info("Orden eliminada -> id={}", orden.getIdOrd());
    }

    @Override
    @Transactional
    public void extornarOrden(Integer id) {
        Orden orden = buscarOrdenConTipoDocumentoYDetalles(id);
        if (orden == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        boolean esEntrada = orden.getTipoDocumento().isTipTd();

        orden.getDetalles().forEach(det -> {
            var articulo = det.getArticulo();
            int nuevoStock = esEntrada
                    ? articulo.getStk() - det.getCantidad()
                    : articulo.getStk() + det.getCantidad();
            articulo.setStk(nuevoStock);
            articuloServicio.guardarArticulo(articulo);
        });

        orden.setEstOrd(8); // Estado = Extornado
        guardarOrden(orden);

        logger.info("Orden extornada -> id={}, numOrd={}", orden.getIdOrd(), orden.getNumOrd());
    }

    @Override
    public boolean validarNumOrdUnico(String numOrd) {
        return !ordenRepositorio.existsByNumOrd(numOrd);
    }

    @Override
    public List<Orden> listarOrdenesPorEstado(Integer estOrd) {
        return ordenRepositorio.findByEstOrd(estOrd);
    }

    @Override
    public List<Orden> listarOrdenesPorEstados(List<Integer> estados) {
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
