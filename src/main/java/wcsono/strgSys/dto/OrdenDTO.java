package wcsono.strgSys.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdenDTO {
    private Integer idOrd;
    private String numOrd;
    private String nomOrd;        // cliente.nomCli
    private String fecOrd;        // fecha ingreso en formato String
    private String estOrd;        // descripción del estado
    private String cssClass;      // ✅ nuevo campo para estilo del estado
    private BigDecimal cosOrd;
    private String tipoDocumento; // tipoDocumento.desTd
    private String fechaEstado;   // fecha/hora del último cambio de estado
    private List<DetalleDTO> detalles;
}
