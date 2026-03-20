package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tipo_documento") // 👈 nombre explícito de la tabla
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTd;

    // Código del tipo de documento (ej. "FAC", "BOL")
    @Column(name = "cod_td", nullable = false, length = 20, unique = true) // 👈 unique para evitar duplicados
    private String codTd;

    // Descripción del tipo de documento (ej. "Factura", "Boleta")
    @Column(name = "des_td", nullable = false, length = 100)
    private String desTd;

    // Indicador de tipo (puede ser boolean o enum, según tu lógica)
    @Column(name = "tip_td", nullable = false)
    private boolean tipTd;

    // Estado del tipo de documento (activo/inactivo)
    @Column(name = "est_td", nullable = false)
    private boolean estTd;
}