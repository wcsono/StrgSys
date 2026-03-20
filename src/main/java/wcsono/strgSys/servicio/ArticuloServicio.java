package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
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

}