package wcsono.strgSys.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.repositorio.FacturacionRepositorio;
import wcsono.strgSys.repositorio.OrdenRepositorio;
import wcsono.strgSys.modelo.Facturacion;
import wcsono.strgSys.modelo.Orden;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FacturacionServicio implements IFacturacionServicio {

    private final FacturacionRepositorio facturacionRepositorio;
    private final OrdenRepositorio ordenRepositorio;

    public FacturacionServicio(FacturacionRepositorio facturacionRepositorio,
                               OrdenRepositorio ordenRepositorio) {
        this.facturacionRepositorio = facturacionRepositorio;
        this.ordenRepositorio = ordenRepositorio;
    }

    @Override
    public List<Facturacion> listarFacturas() {
        return facturacionRepositorio.findAll();
    }

    @Override
    public Optional<Facturacion> buscarPorId(Long idFact) {
        return facturacionRepositorio.findById(idFact);
    }

    @Override
    public List<Facturacion> buscarPorNumFactura(String numFactura) {
        return facturacionRepositorio.findByNumFactura(numFactura);
    }

    @Override
    public List<Facturacion> buscarPorOrden(Integer idOrd) {
        return facturacionRepositorio.findByOrden_IdOrd(idOrd);
    }

    @Override
    @Transactional
    public Facturacion registrarFactura(Integer idOrd, String numFactura) {
        // Buscar la orden
        Orden orden = ordenRepositorio.findById(idOrd)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Crear la factura
        Facturacion facturacion = Facturacion.builder()
                .orden(orden)
                .numFactura(numFactura)
                .monto(orden.getCosOrd())
                .fechaFacturacion(LocalDateTime.now())
                .build();

        // Guardar la factura
        Facturacion nuevaFactura = facturacionRepositorio.save(facturacion);

        // Actualizar estado de la orden a FACTURADA (2)
        orden.setEstOrd(EstadoOrden.FACTURADA);
        ordenRepositorio.save(orden);

        return nuevaFactura;
    }

    @Override
    public void eliminarFactura(Long idFact) {
        facturacionRepositorio.deleteById(idFact);
    }
}
