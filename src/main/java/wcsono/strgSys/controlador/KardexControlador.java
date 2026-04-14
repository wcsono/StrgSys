package wcsono.strgSys.controlador;

import wcsono.strgSys.dto.KardexDTO;
import wcsono.strgSys.servicio.KardexServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KardexControlador {

    private final KardexServicio kardexServicio;

    public KardexControlador(KardexServicio kardexServicio) {
        this.kardexServicio = kardexServicio;
    }

    // Endpoint: /api/kardex/movimientos
    // Devuelve solo artículos con movimiento en los últimos 3 meses
    @GetMapping("/api/kardex/movimientos")
    public List<KardexDTO> getMovimientos() {
        return kardexServicio.obtenerMovimientosUltimos3Meses();
    }

    // Endpoint: /api/kardex/todos
    // Devuelve todos los artículos, incluso los que no tuvieron movimiento
    @GetMapping("/api/kardex/todos")
    public List<KardexDTO> getTodos() {
        return kardexServicio.obtenerTodosUltimos3Meses();
    }
}