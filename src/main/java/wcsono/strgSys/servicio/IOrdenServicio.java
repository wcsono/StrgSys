package wcsono.strgSys.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.enums.EstadoOrden;

import java.time.LocalDate;
import java.util.List;

public interface IOrdenServicio {

    // 🔹 Listados generales
    Page<Orden> listarOrdenesConTipoDocumento(Pageable pageable);
    Page<Orden> listarOrdenes(Pageable pageable);

    // 🔹 Búsquedas
    Orden buscarOrdenPorId(Integer id);
    Orden buscarOrdenConDetalles(Integer id);
    Orden buscarOrdenConTipoDocumentoYDetalles(Integer id);

    // 🔹 Inserción y actualización
    Orden guardarOrden(Orden orden, Cliente cliente, Usuario usuario);
    Orden actualizarOrden(Orden orden, Cliente cliente, Usuario usuario);

    // ✅ Actualización solo de estado (sin usuario)
    Orden actualizarEstadoOrden(Integer idOrd, EstadoOrden nuevoEstado);

    // ✅ Actualización de estado con usuario y fechaEstado
    Orden actualizarEstadoOrden(Orden orden, Usuario usuario);

    List<Orden> listarOrdenesOrdenadasPorFechaDesc();

    List<Orden> listarOrdenesOrdenadasPorIdDesc();



    // 🔹 Eliminación y extorno
    void eliminarOrden(Orden orden);
    void extornarOrden(Integer id);

    // 🔹 Validación
    boolean validarNumOrdUnico(String numOrd);

    // 🔹 Consultas por estado
    List<Orden> listarOrdenesPorEstado(EstadoOrden estOrd);
    List<Orden> listarOrdenesPorEstados(List<EstadoOrden> estados);
    List<Orden> listarOrdenesPorCliente(Integer idCliente);
    List<Orden> listarOrdenesPorUsuario(Integer idUsuario);

    // 🔹 Estadísticas
    List<Object[]> obtenerEntradasVsSalidasPorMes();

    // 🔹 Filtros combinados
    Page<Orden> listarOrdenesFiltradas(
            String numOrd,
            Integer idCliente,
            LocalDate fecOrdDesde,
            LocalDate fecOrdHasta,
            Integer estOrd,   // ⚠️ se mantiene como Integer porque viene del filtro en la vista
            Pageable pageable
    );
}
