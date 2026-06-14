package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import wcsono.strgSys.enums.SubTipoMovimiento;
import wcsono.strgSys.enums.TipoMovimiento;


@Entity
@Table(name = "tipo_documento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTd;

    @Column(name = "cod_td", nullable = false, length = 20, unique = true)
    private String codTd; // Ej: "FAC", "BOL", "ORDC", "VENT"

    @Column(name = "des_td", nullable = false, length = 100)
    private String desTd; // Ej: "Factura", "Boleta", "Orden de Compra"

    // Tipo principal: Ingreso o Salida
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mov", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    // Subtipo: Compra, Venta, Ajuste, etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "subtipo_mov", nullable = false, length = 30)
    private SubTipoMovimiento subTipoMovimiento;

    // Estado del tipo de documento (activo/inactivo según uso)
    @Column(name = "est_td", nullable = false)
    private boolean estTd;
}
