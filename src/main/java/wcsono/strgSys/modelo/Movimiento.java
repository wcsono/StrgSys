package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimiento;

    // Relación con Articulo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idArt", nullable = false)
    private Articulo articulo;

    // Relación con TipoDocumento (entrada, salida, devolución, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_td", nullable = false)
    private TipoDocumento tipoDocumento;

    // Relación con Orden (docRef ya está en Orden)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ord", nullable = false)
    private Orden orden;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorMovimiento;

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    @PrePersist
    @PreUpdate
    public void calcularValorMovimiento() {
        if (cantidad != null && costoUnitario != null) {
            this.valorMovimiento = costoUnitario
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.valorMovimiento = BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
}