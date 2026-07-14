package wcsono.strgSys.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import wcsono.strgSys.modelo.Orden;
import wcsono.strgSys.enums.EstadoOrden;

import java.util.List;

public interface OrdenRepositorio extends JpaRepository<Orden, Integer>, JpaSpecificationExecutor<Orden> {

    // Listar con TipoDocumento
    @Query("SELECT o FROM Orden o JOIN FETCH o.tipoDocumento")
    Page<Orden> listarConTipoDocumento(Pageable pageable);

    // Buscar con detalles
    Orden findByIdOrd(Integer idOrd);

    // Buscar con TipoDocumento y detalles
    @Query("SELECT o FROM Orden o JOIN FETCH o.tipoDocumento td LEFT JOIN FETCH o.detalles WHERE o.idOrd = :idOrd")
    Orden findWithTipoDocumentoAndDetallesByIdOrd(Integer idOrd);

    // Validar numOrd único
    boolean existsByNumOrd(String numOrd);

    // 🔹 Métodos adicionales para IOrdenServicio (ajustados a EstadoOrden)
    List<Orden> findByEstOrd(EstadoOrden estOrd);

    List<Orden> findByEstOrdIn(List<EstadoOrden> estados);

    List<Orden> findByCliente_IdCliente(Integer idCliente);

    List<Orden> findByUsuario_IdUsuario(Integer idUsuario);

    // Listar todas las órdenes ordenadas por ID descendente
    List<Orden> findAllByOrderByIdOrdDesc();

    // Listar todas las órdenes ordenadas por fecha de ingreso descendente
    List<Orden> findAllByOrderByFecOrdDesc();


    // 🔹 Reporte: Entradas vs Salidas por mes
    @Query("SELECT MONTH(o.fecOrd), " +
            "SUM(CASE WHEN o.tipoDocumento.tipoMovimiento = wcsono.strgSys.enums.TipoMovimiento.INGRESO THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN o.tipoDocumento.tipoMovimiento = wcsono.strgSys.enums.TipoMovimiento.SALIDA THEN 1 ELSE 0 END) " +
            "FROM Orden o GROUP BY MONTH(o.fecOrd)")
    List<Object[]> obtenerEntradasVsSalidasPorMes();
}
