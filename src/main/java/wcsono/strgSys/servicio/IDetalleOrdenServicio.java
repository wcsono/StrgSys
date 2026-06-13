package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.DetalleOrden;
import java.util.List;

public interface IDetalleOrdenServicio {

    // 🔹 Listar todos los detalles (sin filtro)
    List<DetalleOrden> listarDetalleOrden();

    // 🔹 Buscar detalle por ID
    DetalleOrden buscarDetalleOrdenPorId(Integer idDo);

    // 🔹 Guardar detalle (insertar o actualizar)
    void guardarDetalleOrden(DetalleOrden detalleOrden);

    // 🔹 Eliminar detalle
    void eliminarDetalleOrden(DetalleOrden detalleOrden);

    // 🔹 Listar detalles de una orden específica (por idOrd)
    List<DetalleOrden> listarPorOrden(Integer idOrd);
}
