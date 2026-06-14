package wcsono.strgSys.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import wcsono.strgSys.enums.EstadoOrden;
import wcsono.strgSys.enums.EstadoOrdenConverter;

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

    // Número de orden calculado (ej: 1000 + idOrd)
    @Column(nullable = false, unique = true, length = 20)
    private String numOrd;

    // Relación con TipoDocumento
    @ManyToOne(optional = false)
    @JoinColumn(name = "idTd", nullable = false)
    private TipoDocumento tipoDocumento;

    // Relación con Usuario
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    // Relación con Cliente
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idCli", nullable = false)
    private Cliente cliente;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fec_ord", nullable = false)
    private LocalDate fecOrd;

    @Size(max = 50)
    private String ndocRef;

    // Estado de la orden (usa enum con converter, columna sigue siendo INT)
    @NotNull
    @Convert(converter = EstadoOrdenConverter.class)
    @Column(name = "est_ord", nullable = false)
    private EstadoOrden estOrd;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cosOrd;

    // Documento de venta asociado
    @Column(length = 50)
    private String docVenta;

    // Relación con DetalleOrden (lado padre)
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleOrden> detalles = new ArrayList<>();
}
