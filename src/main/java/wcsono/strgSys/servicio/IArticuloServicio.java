package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Articulo;
import java.util.List;

public interface IArticuloServicio {

    // Listar todos los artículos
    List<Articulo> listarArticulos();

    // Buscar artículo por ID
    Articulo buscarArticuloPorId(Integer idArticulo);

    // Guardar o actualizar un artículo
    void guardarArticulo(Articulo articulo);

    // Eliminar un artículo
    void eliminarArticulo(Articulo articulo);

    // Validar existencia de un código de artículo
    boolean existeCodigo(String codArt);

    // 👇 Nuevo método: búsqueda con filtros
    List<Articulo> buscarPorFiltros(String codArt, String desArt);
}
