package wcsono.strgSys.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import wcsono.strgSys.enums.EstadoOrden;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrd;

    @Column(name = "num_ord", length = 20, unique = true)
    private String numOrd;

    @Column(name = "fec_ord", nullable = false)
    private LocalDate fecOrd;

    @Enumerated(EnumType.STRING)
    @Column(name = "est_ord", nullable = false)
    private EstadoOrden estOrd;

    @Column(name = "cos_ord", precision = 10, scale = 2)
    private BigDecimal cosOrd;

    // ✅ Campo para fecha/hora del último cambio de estado
    @Column(name = "fec_estado")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEstado;

    // ✅ Ajustado para coincidir con la tabla y la vista
    @Column(name = "ndoc_ref", length = 50)
    private String ndocRef;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_td", nullable = false)
    private TipoDocumento tipoDocumento;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles;
}
