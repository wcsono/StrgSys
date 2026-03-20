package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Articulo;
import java.util.List;

public interface IArticuloServicio {
    List<Articulo> listarArticulos();

    Articulo buscarArticuloPorId(Integer idArticulo);

    void guardarArticulo(Articulo articulo);

    void eliminarArticulo(Articulo articulo);

    boolean existeCodigo(String codArt); // 👈 nuevo método
}