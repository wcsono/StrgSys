package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wcsono.strgSys.modelo.Facturacion;

import java.util.List;

@Repository
public interface FacturacionRepositorio extends JpaRepository<Facturacion, Long> {

    // Buscar facturas por número
    List<Facturacion> findByNumFactura(String numFactura);

    // Buscar facturas por Orden (Orden usa Integer en idOrd)
    List<Facturacion> findByOrden_IdOrd(Integer idOrd);

    // Opcional: obtener todas las facturas de un cliente a través de la orden
    List<Facturacion> findByOrden_Cliente_CodCli(String codCli);
}
