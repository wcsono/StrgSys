package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Facturacion;
import java.util.List;
import java.util.Optional;

public interface IFacturacionServicio {

    // Listar todas las facturas
    List<Facturacion> listarFacturas();

    // Buscar factura por ID (Facturacion usa Long en idFact)
    Optional<Facturacion> buscarPorId(Long idFact);

    // Buscar facturas por número de factura
    List<Facturacion> buscarPorNumFactura(String numFactura);

    // Buscar facturas por Orden (Orden usa Integer en idOrd)
    List<Facturacion> buscarPorOrden(Integer idOrd);

    // Guardar nueva factura y actualizar estado de la orden
    Facturacion registrarFactura(Integer idOrd, String numFactura);

    // Eliminar factura (Facturacion usa Long en idFact)
    void eliminarFactura(Long idFact);

    // ✅ Nuevo: verificar si existen facturas asociadas a una orden
    boolean existenFacturasPorOrden(Integer idOrd);

    // ✅ Nuevo: anular todas las facturas asociadas a una orden
    void anularFacturasPorOrden(Integer idOrd);
}
