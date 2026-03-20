package wcsono.strgSys.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
    @JsonBackReference // 👈 evita ciclos infinitos al serializar Orden → DetalleOrden
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idArt", nullable = false)
    private Articulo articulo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cosArt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null && cosArt != null) {
            this.subtotal = cosArt
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            this.subtotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }
}