package wcsono.strgSys.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wcsono.strgSys.modelo.Orden;

import java.time.LocalDate;
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

    void extornarOrden(Integer id);
    List<Orden> listarOrdenesExtornadas();
    List<Orden> listarOrdenesNoExtornadas();
    List<Orden> listarOrdenesCerradasNoExtornadas();

    boolean validarNumOrdUnico(String numOrd);

    // 🔹 Nuevo método para filtros combinados
    /**
     * Listar órdenes aplicando filtros dinámicos:
     * - numOrd (parcial)
     * - nomOrd (parcial)
     * - rango de fechas (fecOrdDesde, fecOrdHasta)
     * - estado (estOrd: abierta/cerrada)
     */
    Page<Orden> listarOrdenesFiltradas(String numOrd,
                                       String nomOrd,
                                       LocalDate fecOrdDesde,
                                       LocalDate fecOrdHasta,
                                       Boolean estOrd,
                                       Pageable pageable);
}