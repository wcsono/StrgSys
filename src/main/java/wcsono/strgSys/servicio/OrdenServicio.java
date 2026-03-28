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
        logger.debug("Service: listarOrdenesConTipoDocumento -> page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Orden> page = ordenRepositorio.listarConTipoDocumento(pageable);
        logger.debug("Service result -> totalElements={}, totalPages={}, contentSize={}",
                page.getTotalElements(), page.getTotalPages(), page.getContent().size());
        return page;
    }

    @Override
    public Page<Orden> listarOrdenes(Pageable pageable) {
        logger.debug("Service: listarOrdenes -> page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Orden> page = ordenRepositorio.findAll(pageable);
        logger.debug("Service result -> totalElements={}, totalPages={}, contentSize={}",
                page.getTotalElements(), page.getTotalPages(), page.getContent().size());
        return page;
    }

    @Override
    public Orden buscarOrdenPorId(Integer id) {
        return ordenRepositorio.findById(id).orElse(null);
    }

    @Override
    public Orden buscarOrdenConDetalles(Integer id) {
        return ordenRepositorio.findByIdOrd(id).orElse(null);
    }

    @Override
    public Orden buscarOrdenConTipoDocumentoYDetalles(Integer id) {
        return ordenRepositorio.findWithTipoDocumentoAndDetallesByIdOrd(id).orElse(null);
    }

    @Override
    public Orden guardarOrden(Orden orden) {
        Orden saved = ordenRepositorio.save(orden);
        logger.info("Orden guardada -> id={}, numOrd={}", saved.getIdOrd(), saved.getNumOrd());
        return saved;
    }

    @Override
    public void eliminarOrden(Orden orden) {
        ordenRepositorio.delete(orden);
        logger.info("Orden eliminada -> id={}", orden.getIdOrd());
    }

    // 🔹 Extornar orden
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
                    ? articulo.getStk() - det.getCantidad() // inverso de entrada
                    : articulo.getStk() + det.getCantidad(); // inverso de salida

            articulo.setStk(nuevoStock);
            articuloServicio.guardarArticulo(articulo);

            logger.info("Artículo {} extornado: stock={}, costo={}",
                    articulo.getCodArt(), articulo.getStk(), articulo.getCosto());
        });

        orden.setExtornada(true);
        orden.setEstOrd(false);

        guardarOrden(orden);
        logger.info("Orden extornada -> id={}, numOrd={}", orden.getIdOrd(), orden.getNumOrd());
    }

    // 🔹 Consultas adicionales
    @Override
    public List<Orden> listarOrdenesExtornadas() {
        return ordenRepositorio.findByExtornadaTrue();
    }

    @Override
    public List<Orden> listarOrdenesNoExtornadas() {
        return ordenRepositorio.findByExtornadaFalse();
    }

    @Override
    public List<Orden> listarOrdenesCerradasNoExtornadas() {
        return ordenRepositorio.findByEstOrdTrueAndExtornadaFalse();
    }

    @Override
    public boolean validarNumOrdUnico(String numOrd) {
        return !ordenRepositorio.existsByNumOrd(numOrd);
    }

    // 🔹 Filtros combinados
    @Override
    public Page<Orden> listarOrdenesFiltradas(
            String numOrd,
            String nomOrd,
            LocalDate fecOrdDesde,
            LocalDate fecOrdHasta,
            Boolean estOrd,
            Pageable pageable) {

        Specification<Orden> spec = (root, query, cb) -> cb.conjunction();

        if (numOrd != null && !numOrd.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("numOrd")), "%" + numOrd.toLowerCase() + "%"));
        }

        if (nomOrd != null && !nomOrd.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nomOrd")), "%" + nomOrd.toLowerCase() + "%"));
        }

        if (fecOrdDesde != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("fecOrd"), fecOrdDesde));
        }

        if (fecOrdHasta != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("fecOrd"), fecOrdHasta));
        }

        if (estOrd != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estOrd"), estOrd));
        }

        Page<Orden> page = ordenRepositorio.findAll(spec, pageable);
        logger.debug("Filtros aplicados -> numOrd={}, nomOrd={}, fecOrdDesde={}, fecOrdHasta={}, estOrd={}",
                numOrd, nomOrd, fecOrdDesde, fecOrdHasta, estOrd);
        logger.debug("Service result -> totalElements={}, totalPages={}, contentSize={}",
                page.getTotalElements(), page.getTotalPages(), page.getContent().size());

        return page;
    }
}