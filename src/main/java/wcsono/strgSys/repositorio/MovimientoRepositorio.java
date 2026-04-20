package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wcsono.strgSys.modelo.Movimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepositorio extends JpaRepository<Movimiento, Integer> {

    // Listar todos los movimientos de un artículo específico
    List<Movimiento> findByArticulo_IdArt(Integer idArt);

    // Listar movimientos por rango de fechas
    List<Movimiento> findByFechaMovimientoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Ejemplo de query personalizada: movimientos de un mes específico
    @Query("SELECT m FROM Movimiento m WHERE MONTH(m.fechaMovimiento) = ?1 AND YEAR(m.fechaMovimiento) = ?2")
    List<Movimiento> findByMesYAño(int mes, int anio);
}