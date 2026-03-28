package wcsono.strgSys.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "detalles")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrd;

    @NotBlank
    @Column(nullable = false, length = 20, unique = true)
    private String numOrd;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nomOrd;

    // Relación con TipoDocumento
    @ManyToOne(optional = false)
    @JoinColumn(name = "idTd", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fec_ord", nullable = false)
    private LocalDate fecOrd;

    @Size(max = 50)
    private String ndocRef;

    @Column(nullable = false)
    private boolean estOrd = false;

    @Column(nullable = false)
    private boolean extornada = false;   // true = extornada

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cosOrd;

    // Relación con DetalleOrden (lado padre)
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleOrden> detalles = new ArrayList<>();
}