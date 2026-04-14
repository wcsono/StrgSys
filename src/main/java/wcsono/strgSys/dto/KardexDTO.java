package wcsono.strgSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data                   // Genera getters, setters, equals, hashCode y toString
@NoArgsConstructor      // Constructor vacío
@AllArgsConstructor     // Constructor con todos los campos
public class KardexDTO {

    private String articulo;
    private int anio;
    private int mes;
    private int entradas;
    private int salidas;
    private int saldoFinal;
    private BigDecimal valorMovido;

    // Constructor especial para calcular saldo automáticamente
    public KardexDTO(String articulo, int anio, int mes,
                     int entradas, int salidas, BigDecimal valorMovido) {
        this.articulo = articulo;
        this.anio = anio;
        this.mes = mes;
        this.entradas = entradas;
        this.salidas = salidas;
        this.saldoFinal = entradas - salidas;
        this.valorMovido = valorMovido;
    }
}