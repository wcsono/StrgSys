package wcsono.strgSys.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "detalles")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrd;

    @Column(nullable = false, length = 20)
    private String numOrd;

    @Column(nullable = false, length = 100)
    private String nomOrd;

    // Relación con TipoDocumento
    @ManyToOne(optional = false)
    @JoinColumn(name = "idTd", nullable = false)
    private TipoDocumento tipoDocumento;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date fecOrd;

    @Column(length = 50)
    private String ndocRef;

    private boolean estOrd;
    private boolean extornada = false;   // true = extornada


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cosOrd;

    // Relación con DetalleOrden (lado padre)
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleOrden> detalles = new ArrayList<>();
}