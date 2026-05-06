package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "detalleOrdenes")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArt;

    @Column(nullable = false, length = 20, unique = true)
    private String codArt;

    @Column(nullable = false, length = 100)
    private String desArt;

    @Column(length = 10)
    private String udm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date fecReg;

    @Column(nullable = false)
    private Integer stk;

    @Column(nullable = false)
    private BigDecimal costo;

    private boolean estArt;

    @Column(length = 50)
    private String ubiArt;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 👈 evita que Jackson serialice detalleOrdenes dentro de Articulo
    private List<DetalleOrden> detalleOrdenes = new ArrayList<>();
}