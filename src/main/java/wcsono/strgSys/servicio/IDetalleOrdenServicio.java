package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.DetalleOrden;
import java.util.List;

public interface IDetalleOrdenServicio {

    // 🔹 Listar todos los detalles
    List<DetalleOrden> listarDetalleOrden();

    // 🔹 Buscar detalle por ID
    DetalleOrden buscarDetalleOrdenPorId(Integer idDo);

    // 🔹 Guardar detalle
    void guardarDetalleOrden(DetalleOrden detalleOrden);

    // 🔹 Eliminar detalle
    void eliminarDetalleOrden(DetalleOrden detalleOrden);

    // 🔹 Listar detalles de una orden específica
    List<DetalleOrden> listarPorOrden(Integer idOrd);
}