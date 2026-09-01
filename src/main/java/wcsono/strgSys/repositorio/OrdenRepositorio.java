package wcsono.strgSys.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Orden findWithTipoDocumentoAndDetallesByIdOrd(@Param("idOrd") Integer idOrd);

    // Validar numOrd único
    boolean existsByNumOrd(String numOrd);

    // Consultas por estado
    List<Orden> findByEstOrd(EstadoOrden estOrd);
    List<Orden> findByEstOrdIn(List<EstadoOrden> estados);
    List<Orden> findByCliente_IdCliente(Integer idCliente);
    List<Orden> findByUsuario_IdUsuario(Integer idUsuario);

    // Ordenadas
    List<Orden> findAllByOrderByIdOrdDesc();
    List<Orden> findAllByOrderByFecOrdDesc();

    // 🔹 Órdenes cerradas en el mes actual
    @Query("SELECT COUNT(o) FROM Orden o " +
            "WHERE o.estOrd = wcsono.strgSys.enums.EstadoOrden.CERRADA " +
            "AND YEAR(o.fecOrd) = YEAR(CURRENT_DATE) " +
            "AND MONTH(o.fecOrd) = MONTH(CURRENT_DATE)")
    Long contarOrdenesCerradasMesActual();

    // 🔹 Órdenes pendientes de cierre (ENTREGADA o INGRESADA)
    @Query("SELECT COUNT(o) FROM Orden o " +
            "WHERE o.estOrd IN (wcsono.strgSys.enums.EstadoOrden.ENTREGADA, wcsono.strgSys.enums.EstadoOrden.INGRESADA)")
    Long contarOrdenesPendientes();

    // 🔹 Query para obtener el Top de productos más vendidos
    // Considera únicamente Órdenes en estado CERRADA,
    // con TipoMovimiento = SALIDA y SubTipoMovimiento = VENTA.
    @Query("SELECT d.articulo.desArt, SUM(d.cantidad) " +
            "FROM DetalleOrden d " +
            "JOIN d.orden o " +
            "WHERE o.estOrd = wcsono.strgSys.enums.EstadoOrden.CERRADA " +
            "AND o.tipoDocumento.tipoMovimiento = wcsono.strgSys.enums.TipoMovimiento.SALIDA " +
            "AND o.tipoDocumento.subTipoMovimiento = wcsono.strgSys.enums.SubTipoMovimiento.VENTA " +
            "GROUP BY d.articulo.desArt " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> obtenerTopProductosVendidos();

    // 🔹 Ventas por mes (para gráfico)
    @Query("SELECT MONTH(o.fecOrd), SUM(d.cantidad) " +
            "FROM DetalleOrden d " +
            "JOIN d.orden o " +
            "WHERE o.estOrd = wcsono.strgSys.enums.EstadoOrden.CERRADA " +
            "AND o.tipoDocumento.tipoMovimiento = wcsono.strgSys.enums.TipoMovimiento.SALIDA " +
            "AND o.tipoDocumento.subTipoMovimiento = wcsono.strgSys.enums.SubTipoMovimiento.VENTA " +
            "GROUP BY MONTH(o.fecOrd) " +
            "ORDER BY MONTH(o.fecOrd)")
    List<Object[]> obtenerVentasPorMes();
}
