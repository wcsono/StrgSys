package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.Articulo;
import wcsono.strgSys.repositorio.ArticuloRepositorio;

import java.util.List;

@Service
public class ArticuloServicio implements IArticuloServicio {

    @Autowired
    private ArticuloRepositorio articuloRepositorio;

    @Override
    public List<Articulo> listarArticulos() {
        return articuloRepositorio.findAll();
    }

    @Override
    public Articulo buscarArticuloPorId(Integer idArticulo) {
        return articuloRepositorio.findById(idArticulo).orElse(null);
    }

    @Override
    public void guardarArticulo(Articulo articulo) {
        articuloRepositorio.save(articulo);
    }

    @Override
    public void eliminarArticulo(Articulo articulo) {
        articuloRepositorio.delete(articulo);
    }

    @Override
    public boolean existeCodigo(String codArt) {
        return articuloRepositorio.existsByCodArt(codArt.toUpperCase());
    }

    // 👇 Nuevo método: búsqueda con filtros (Código, Descripción y Ubicación) con paginación
    @Override
    public Page<Articulo> buscarPorFiltros(String codArt, String desArt, String ubiArt, Pageable pageable) {
        // Evitamos valores nulos para que la consulta funcione correctamente
        if (codArt == null) codArt = "";
        if (desArt == null) desArt = "";
        if (ubiArt == null) ubiArt = "";

        return articuloRepositorio.findByFiltros(codArt, desArt, ubiArt, pageable);
    }

    public long contarArticulos() {
        return articuloRepositorio.count();
    }

    @Override
    public Double obtenerValorTotalInventario() {
        Double total = articuloRepositorio.calcularValorTotalInventario();
        return total != null ? total : 0.0;
    }


}
