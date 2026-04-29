package wcsono.strgSys.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import wcsono.strgSys.modelo.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenRepositorio extends JpaRepository<Orden, Integer>, JpaSpecificationExecutor<Orden> {

    /**
     * Consulta paginada explícita + countQuery para evitar discrepancias.
     * Se usa LEFT JOIN FETCH para traer el tipoDocumento en la misma consulta
     * y evitar duplicados que luego Hibernate deduplicaba en memoria.
     */
    @Query(value = "SELECT o FROM Orden o LEFT JOIN FETCH o.tipoDocumento",
            countQuery = "SELECT COUNT(o) FROM Orden o")
    Page<Orden> listarConTipoDocumento(Pageable pageable);

    /**
     * Buscar por id y traer detalles (EntityGraph para forzar fetch).
     */
    @EntityGraph(attributePaths = {"detalles"})
    Optional<Orden> findByIdOrd(Integer id);

    /**
     * Buscar por id y traer tipoDocumento + detalles.
     */
    @EntityGraph(attributePaths = {"tipoDocumento", "detalles"})
    Optional<Orden> findWithTipoDocumentoAndDetallesByIdOrd(Integer id);

    // 🔹 Métodos derivados para extornos y estados
    List<Orden> findByExtornadaTrue();
    List<Orden> findByExtornadaFalse();
    List<Orden> findByEstOrdTrueAndExtornadaFalse();

    boolean existsByNumOrd(String numOrd);

    // 🔹 Reporte: Entradas vs Salidas por mes (solo órdenes cerradas)
    @Query("SELECT YEAR(o.fecOrd) as anio, MONTH(o.fecOrd) as mes, " +
            "SUM(CASE WHEN o.tipoDocumento.tipTd = true THEN d.cantidad ELSE 0 END) as entradas, " +
            "SUM(CASE WHEN o.tipoDocumento.tipTd = false THEN d.cantidad ELSE 0 END) as salidas " +
            "FROM Orden o JOIN o.detalles d " +
            "WHERE o.estOrd = true " + // ✅ solo órdenes cerradas
            "GROUP BY YEAR(o.fecOrd), MONTH(o.fecOrd) " +
            "ORDER BY anio, mes")
    List<Object[]> obtenerEntradasVsSalidasPorMes();
}
