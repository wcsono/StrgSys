package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturacion")
@Data // 🔹 Genera getters, setters, equals, hashCode y toString
@NoArgsConstructor
@AllArgsConstructor
@Builder // 🔹 Habilita el patrón builder
public class Facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFact; // Facturación usa Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrd", nullable = false)
    private Orden orden; // Orden usa Integer en idOrd

    @Column(nullable = false, length = 20)
    private String numFactura;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fechaFacturacion;
}
