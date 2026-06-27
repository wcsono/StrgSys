package wcsono.strgSys.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleDTO {
    private String codArt;        // articulo.codArt
    private String articulo;      // articulo.desArt
    private String ubicacion;     // articulo.ubiArt
    private Integer cantidad;
    private BigDecimal costo;     // articulo.costo
    private BigDecimal precioVenta;
    private BigDecimal subtotal;
}
