package wcsono.strgSys.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import wcsono.strgSys.enums.SubTipoMovimiento;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orden", "articulo"})
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ord", nullable = false)
    @JsonBackReference
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idArt", nullable = false)
    private Articulo articulo;

    @Column(nullable = false)
    private Integer cantidad;

    // Costo unitario (para compras/ingresos)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cosArt;

    // Precio de venta unitario (para ventas)
    @Column(nullable = true, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    // Subtotal calculado automáticamente
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null) {
            BigDecimal precioBase;

            // ✅ Condicionamos según el subtipo de movimiento de la orden
            if (orden != null && orden.getTipoDocumento() != null
                    && orden.getTipoDocumento().getSubTipoMovimiento() == SubTipoMovimiento.VENTA) {
                precioBase = precioVenta != null ? precioVenta : BigDecimal.ZERO;
            } else {
                precioBase = cosArt != null ? cosArt : BigDecimal.ZERO;
            }

            this.subtotal = precioBase
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            this.subtotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
