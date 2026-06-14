package wcsono.strgSys.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdenDTO {
    private Integer idOrd;
    private String numOrd;
    private String nomOrd;        // cliente.nomCli
    private String fecOrd;        // fecha en formato String
    private String estOrd;        // ✅ ahora es String (descripción del estado)
    private BigDecimal cosOrd;
    private String tipoDocumento; // tipoDocumento.desTd
    private List<DetalleDTO> detalles;
}
