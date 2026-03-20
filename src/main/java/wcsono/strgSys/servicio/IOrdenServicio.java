package wcsono.strgSys.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wcsono.strgSys.modelo.Orden;

import java.util.List;

public interface IOrdenServicio {

    /**
     * Listar órdenes con su TipoDocumento (consulta paginada).
     */
    Page<Orden> listarOrdenesConTipoDocumento(Pageable pageable);

    /**
     * Listar órdenes sin joins adicionales (consulta paginada).
     */
    Page<Orden> listarOrdenes(Pageable pageable);

    /**
     * Buscar una orden por su ID.
     */
    Orden buscarOrdenPorId(Integer id);

    /**
     * Buscar una orden y traer sus detalles.
     */
    Orden buscarOrdenConDetalles(Integer id);

    /**
     * Buscar una orden y traer TipoDocumento + detalles.
     */
    Orden buscarOrdenConTipoDocumentoYDetalles(Integer id);

    /**
     * Guardar o actualizar una orden.
     */
    Orden guardarOrden(Orden orden);

    /**
     * Eliminar una orden.
     */
    void eliminarOrden(Orden orden);

    // 🔹 Nuevos métodos para manejar extornos

    /**
     * Extornar una orden: revertir stock y marcarla como extornada.
     */
    void extornarOrden(Integer id);

    /**
     * Listar todas las órdenes extornadas.
     */
    List<Orden> listarOrdenesExtornadas();

    /**
     * Listar todas las órdenes no extornadas.
     */
    List<Orden> listarOrdenesNoExtornadas();

    /**
     * Listar órdenes cerradas y no extornadas.
     */
    List<Orden> listarOrdenesCerradasNoExtornadas();

    boolean validarNumOrdUnico(String numOrd);

}