package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wcsono.strgSys.modelo.Orden;

import java.time.LocalDate;
import java.util.List;

public interface KardexRepositorio extends JpaRepository<Orden, Integer> {

    // Solo artículos con movimiento (últimos 3 meses)
    @Query(value = "SELECT a.des_art, YEAR(o.fec_ord), MONTH(o.fec_ord), " +
            "SUM(CASE WHEN td.tip_td = 1 THEN d.cantidad ELSE 0 END), " +
            "SUM(CASE WHEN td.tip_td = 0 THEN d.cantidad ELSE 0 END), " +
            "SUM(d.subtotal) " +
            "FROM orden o " +
            "JOIN detalle_orden d ON d.id_ord = o.id_ord " +
            "JOIN articulo a ON d.id_art = a.id_art " +   // 👈 corregido
            "JOIN tipo_documento td ON o.id_td = td.id_td " +
            "WHERE o.fec_ord >= :fechaInicio " +
            "GROUP BY a.des_art, YEAR(o.fec_ord), MONTH(o.fec_ord) " +
            "ORDER BY YEAR(o.fec_ord), MONTH(o.fec_ord)",
            nativeQuery = true)
    List<Object[]> obtenerKardexMovimientos(@Param("fechaInicio") LocalDate fechaInicio);

    // Todos los artículos (incluye sin movimiento, últimos 3 meses)
    @Query(value = "SELECT a.des_art, YEAR(o.fec_ord), MONTH(o.fec_ord), " +
            "COALESCE(SUM(CASE WHEN td.tip_td = 1 THEN d.cantidad ELSE 0 END),0), " +
            "COALESCE(SUM(CASE WHEN td.tip_td = 0 THEN d.cantidad ELSE 0 END),0), " +
            "COALESCE(SUM(d.subtotal),0) " +
            "FROM articulo a " +
            "LEFT JOIN detalle_orden d ON d.id_art = a.id_art " +   // 👈 corregido
            "LEFT JOIN orden o ON d.id_ord = o.id_ord AND o.fec_ord >= :fechaInicio " +
            "LEFT JOIN tipo_documento td ON o.id_td = td.id_td " +
            "GROUP BY a.des_art, YEAR(o.fec_ord), MONTH(o.fec_ord) " +
            "ORDER BY YEAR(o.fec_ord), MONTH(o.fec_ord)",
            nativeQuery = true)
    List<Object[]> obtenerKardexTodos(@Param("fechaInicio") LocalDate fechaInicio);
}