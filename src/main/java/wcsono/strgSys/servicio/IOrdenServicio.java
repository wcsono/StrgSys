package wcsono.strgSys.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wcsono.strgSys.modelo.Orden;

import java.time.LocalDate;
import java.util.List;

public interface IOrdenServicio {

    Page<Orden> listarOrdenesConTipoDocumento(Pageable pageable);

    Page<Orden> listarOrdenes(Pageable pageable);

    Orden buscarOrdenPorId(Integer id);

    Orden buscarOrdenConDetalles(Integer id);

    Orden buscarOrdenConTipoDocumentoYDetalles(Integer id);

    Orden guardarOrden(Orden orden);

    void eliminarOrden(Orden orden);

    // 🔹 Métodos para estados
    List<Orden> listarOrdenesPorEstado(Integer estOrd);
    List<Orden> listarOrdenesPorEstados(List<Integer> estados);

    // 🔹 Métodos para Cliente y Usuario
    List<Orden> listarOrdenesPorCliente(Integer idCliente);
    List<Orden> listarOrdenesPorUsuario(Integer idUsuario);

    boolean validarNumOrdUnico(String numOrd);

    // 🔹 Filtros combinados (ajustados)
    Page<Orden> listarOrdenesFiltradas(String numOrd,
                                       Integer idCliente,
                                       LocalDate fecOrdDesde,
                                       LocalDate fecOrdHasta,
                                       Integer estOrd,
                                       Pageable pageable);

    // 🔹 Reporte: Entradas vs Salidas por mes
    List<Object[]> obtenerEntradasVsSalidasPorMes();

    void extornarOrden(Integer id);

}
