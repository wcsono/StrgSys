package wcsono.strgSys.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wcsono.strgSys.modelo.Facturacion;
import wcsono.strgSys.enums.EstadoFactura;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.repositorio.FacturacionRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FacturacionServicio implements IFacturacionServicio {

    private final Logger logger = LoggerFactory.getLogger(FacturacionServicio.class);

    @Autowired
    private FacturacionRepositorio facturacionRepositorio;

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
        Facturacion factura = new Facturacion();
        factura.setNumFactura(numFactura);
        factura.setEstado(EstadoFactura.ACTIVA);

        // ✅ Asignar fecha de facturación para evitar null
        factura.setFechaFacturacion(LocalDateTime.now());

        // 🔹 Relación con la orden (usa idOrd como referencia)
        Orden orden = new Orden();
        orden.setIdOrd(idOrd);
        factura.setOrden(orden);

        Facturacion saved = facturacionRepositorio.save(factura);
        logger.info("Factura registrada -> id={}, numFactura={}, orden={}",
                saved.getIdFact(), saved.getNumFactura(), idOrd);
        return saved;
    }

    @Override
    @Transactional
    public void eliminarFactura(Long idFact) {
        facturacionRepositorio.deleteById(idFact);
        logger.info("Factura eliminada -> id={}", idFact);
    }

    // ✅ Verificar si existen facturas asociadas a una orden
    @Override
    public boolean existenFacturasPorOrden(Integer idOrd) {
        return facturacionRepositorio.existsByOrden_IdOrd(idOrd);
    }

    // ✅ Anular todas las facturas asociadas a una orden
    @Override
    @Transactional
    public void anularFacturasPorOrden(Integer idOrd) {
        List<Facturacion> facturas = facturacionRepositorio.findByOrden_IdOrd(idOrd);
        for (Facturacion factura : facturas) {
            factura.setEstado(EstadoFactura.ANULADA);
            facturacionRepositorio.save(factura);
        }
        logger.info("Facturas anuladas para orden -> id={}", idOrd);
    }
}
