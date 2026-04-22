package wcsono.strgSys.servicio;

import org.springframework.stereotype.Service;
import wcsono.strgSys.dto.MovimientoReporteDTO;
import wcsono.strgSys.dto.MovimientoDTO;
import wcsono.strgSys.modelo.Movimiento;
import wcsono.strgSys.repositorio.MovimientoRepositorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoServicio {

    private final MovimientoRepositorio movimientoRepositorio;

    public MovimientoServicio(MovimientoRepositorio movimientoRepositorio) {
        this.movimientoRepositorio = movimientoRepositorio;
    }

    // =========================
    // Métodos CRUD básicos
    // =========================

    public List<Movimiento> listarTodos() {
        return movimientoRepositorio.findAll();
    }

    public List<Movimiento> listarPorArticulo(Integer idArt) {
        return movimientoRepositorio.findByArticulo_IdArt(idArt);
    }

    public List<Movimiento> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepositorio.findByFechaMovimientoBetween(inicio, fin);
    }

    public List<Movimiento> listarPorMesYAño(int mes, int anio) {
        return movimientoRepositorio.findByMesYAño(mes, anio);
    }

    public Movimiento guardarMovimiento(Movimiento movimiento) {
        return movimientoRepositorio.save(movimiento);
    }

    // =========================
    // Método para Reportes DTO
    // =========================

    public List<MovimientoReporteDTO> listarReporteDTO() {
        return movimientoRepositorio.findAll().stream()
                .map(mov -> {
                    // ✅ Usamos TipoDocumento para determinar entrada/salida
                    int entradas = mov.getTipoDocumento().isTipTd() ? mov.getCantidad() : 0;
                    int salidas = !mov.getTipoDocumento().isTipTd() ? mov.getCantidad() : 0;

                    // ✅ Multiplicación segura con BigDecimal
                    BigDecimal valorMovido = BigDecimal.valueOf(mov.getCantidad())
                            .multiply(mov.getCostoUnitario());

                    return new MovimientoReporteDTO(
                            mov.getArticulo().getDesArt(),          // nombre del artículo
                            mov.getFechaMovimiento().getYear(),     // año
                            mov.getFechaMovimiento().getMonthValue(), // mes
                            entradas,
                            salidas,
                            mov.getArticulo().getStk(),             // saldo final
                            valorMovido.doubleValue()               // valor movido
                    );
                })
                .collect(Collectors.toList());
    }

    // =========================
    // Método para Movimientos Ordenados DTO
    // =========================

    public List<MovimientoDTO> listarMovimientosOrdenados() {
        return movimientoRepositorio.findAll().stream()
                .sorted((m1, m2) -> {
                    int cmpArt = m1.getArticulo().getDesArt()
                            .compareToIgnoreCase(m2.getArticulo().getDesArt());
                    if (cmpArt != 0) return cmpArt;
                    int cmpAnio = Integer.compare(m2.getFechaMovimiento().getYear(),
                            m1.getFechaMovimiento().getYear());
                    if (cmpAnio != 0) return cmpAnio;
                    return Integer.compare(m2.getFechaMovimiento().getMonthValue(),
                            m1.getFechaMovimiento().getMonthValue());
                })
                .map(mov -> new MovimientoDTO(
                        mov.getArticulo().getDesArt(),
                        mov.getFechaMovimiento().getYear(),
                        mov.getFechaMovimiento().getMonthValue(),
                        mov.getTipoDocumento().isTipTd() ? "Entrada" : "Salida",
                        mov.getCantidad(),
                        mov.getCostoUnitario().doubleValue()
                ))
                .collect(Collectors.toList());
    }
}
