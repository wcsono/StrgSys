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

    Page<Orden> listarOrdenesConTipoDocumento(Pageable pageable);

    Page<Orden> listarOrdenes(Pageable pageable);

    Orden buscarOrdenPorId(Integer id);

    Orden buscarOrdenConDetalles(Integer id);

    Orden buscarOrdenConTipoDocumentoYDetalles(Integer id);

    // ✅ Inserción de nueva orden (siempre inicia en INICIAL)
    Orden guardarOrden(Orden orden, Cliente cliente, Usuario usuario);

    // ✅ Actualización general de la orden (datos completos)
    Orden actualizarOrden(Orden orden, Cliente cliente, Usuario usuario);

    // ✅ Nuevo método para actualizar solo el estado y fechaEstado
    Orden actualizarEstadoOrden(Integer idOrd, EstadoOrden nuevoEstado);

    // ✅ Eliminación física directa
    void eliminarOrden(Orden orden);

    // ✅ Extornar orden (cambia estado a EXTORNADA)
    void extornarOrden(Integer id);

    boolean validarNumOrdUnico(String numOrd);

    // 🔹 Consultas por estado
    List<Orden> listarOrdenesPorEstado(EstadoOrden estOrd);

    // ✅ Nuevo método para actualizar estado con usuario y fechaEstado
    Orden actualizarEstadoOrden(Orden orden, Usuario usuario);


    List<Orden> listarOrdenesPorEstados(List<EstadoOrden> estados);

    List<Orden> listarOrdenesPorCliente(Integer idCliente);

    List<Orden> listarOrdenesPorUsuario(Integer idUsuario);

    List<Object[]> obtenerEntradasVsSalidasPorMes();

    // 🔹 Filtros combinados
    Page<Orden> listarOrdenesFiltradas(
            String numOrd,
            Integer idCliente,
            LocalDate fecOrdDesde,
            LocalDate fecOrdHasta,
            Integer estOrd,   // ⚠️ se mantiene como Integer porque viene del filtro en la vista
            Pageable pageable);
}
