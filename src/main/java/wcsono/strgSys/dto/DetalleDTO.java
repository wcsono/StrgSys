package wcsono.strgSys.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleDTO {
    private String articulo;      // articulo.desArt
    private Integer cantidad;
    private BigDecimal cosArt;
    private BigDecimal subtotal;
}
