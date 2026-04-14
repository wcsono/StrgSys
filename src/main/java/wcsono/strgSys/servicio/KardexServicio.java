package wcsono.strgSys.servicio;

import wcsono.strgSys.dto.KardexDTO;
import wcsono.strgSys.repositorio.KardexRepositorio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KardexServicio {

    private final KardexRepositorio kardexRepositorio;

    public KardexServicio(KardexRepositorio kardexRepositorio) {
        this.kardexRepositorio = kardexRepositorio;
    }

    // Solo artículos con movimiento (últimos 3 meses)
    public List<KardexDTO> obtenerMovimientosUltimos3Meses() {
        LocalDate fechaInicio = LocalDate.now().minusMonths(3);
        return kardexRepositorio.obtenerKardexMovimientos(fechaInicio)
                .stream()
                .map(r -> {
                    String articulo = (String) r[0];
                    int anio = r[1] != null ? ((Number) r[1]).intValue() : LocalDate.now().getYear();
                    int mes = r[2] != null ? ((Number) r[2]).intValue() : LocalDate.now().getMonthValue();
                    int entradas = r[3] != null ? ((Number) r[3]).intValue() : 0;
                    int salidas = r[4] != null ? ((Number) r[4]).intValue() : 0;
                    BigDecimal valorMovido = r[5] != null ? (BigDecimal) r[5] : BigDecimal.ZERO;

                    return new KardexDTO(
                            articulo,
                            anio,
                            mes,
                            entradas,
                            salidas,
                            valorMovido
                    );
                })
                .collect(Collectors.toList());
    }

    // Todos los artículos (incluye sin movimiento, últimos 3 meses)
    public List<KardexDTO> obtenerTodosUltimos3Meses() {
        LocalDate fechaInicio = LocalDate.now().minusMonths(3);
        return kardexRepositorio.obtenerKardexTodos(fechaInicio)
                .stream()
                .map(r -> {
                    String articulo = (String) r[0];
                    int anio = r[1] != null ? ((Number) r[1]).intValue() : LocalDate.now().getYear();
                    int mes = r[2] != null ? ((Number) r[2]).intValue() : LocalDate.now().getMonthValue();
                    int entradas = r[3] != null ? ((Number) r[3]).intValue() : 0;
                    int salidas = r[4] != null ? ((Number) r[4]).intValue() : 0;
                    BigDecimal valorMovido = r[5] != null ? (BigDecimal) r[5] : BigDecimal.ZERO;

                    return new KardexDTO(
                            articulo,
                            anio,
                            mes,
                            entradas,
                            salidas,
                            valorMovido
                    );
                })
                .collect(Collectors.toList());
    }
}