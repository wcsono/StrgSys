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

    Orden guardarOrden(Orden orden, Cliente cliente, Usuario usuario);

    // ✅ Nuevo método para actualizar orden existente
    Orden actualizarOrden(Orden orden, Cliente cliente, Usuario usuario);

    // ✅ Eliminación física directa
    void eliminarOrden(Orden orden);

    void extornarOrden(Integer id);

    boolean validarNumOrdUnico(String numOrd);

    // 🔹 Ajustados a EstadoOrden
    List<Orden> listarOrdenesPorEstado(EstadoOrden estOrd);

    List<Orden> listarOrdenesPorEstados(List<EstadoOrden> estados);

    List<Orden> listarOrdenesPorCliente(Integer idCliente);

    List<Orden> listarOrdenesPorUsuario(Integer idUsuario);

    List<Object[]> obtenerEntradasVsSalidasPorMes();

    Page<Orden> listarOrdenesFiltradas(
            String numOrd,
            Integer idCliente,
            LocalDate fecOrdDesde,
            LocalDate fecOrdHasta,
            Integer estOrd,   // ⚠️ este se mantiene como Integer porque viene del filtro en la vista
            Pageable pageable);
}
