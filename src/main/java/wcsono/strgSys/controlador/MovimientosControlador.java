package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wcsono.strgSys.modelo.Movimiento;
import wcsono.strgSys.dto.MovimientoReporteDTO;
import wcsono.strgSys.servicio.MovimientoServicio;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientosControlador {

    @Autowired
    private MovimientoServicio movimientoServicio;

    /**
     * Listar todos los movimientos crudos
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Movimiento>> listarTodos() {
        List<Movimiento> movimientos = movimientoServicio.listarTodos();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Listar movimientos por artículo
     */
    @GetMapping("/articulo/{idArt}")
    public ResponseEntity<List<Movimiento>> listarPorArticulo(@PathVariable Integer idArt) {
        List<Movimiento> movimientos = movimientoServicio.listarPorArticulo(idArt);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Listar movimientos por rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<List<Movimiento>> listarPorRangoFechas(
            @RequestParam String inicio,
            @RequestParam String fin) {
        List<Movimiento> movimientos = movimientoServicio.listarPorRangoFechas(
                LocalDateTime.parse(inicio),
                LocalDateTime.parse(fin)
        );
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Listar movimientos por mes y año
     */
    @GetMapping("/mes")
    public ResponseEntity<List<Movimiento>> listarPorMesYAño(
            @RequestParam int mes,
            @RequestParam int anio) {
        List<Movimiento> movimientos = movimientoServicio.listarPorMesYAño(mes, anio);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Listar movimientos resumidos para reportes (DTO)
     */
    @GetMapping("/reportes")
    public ResponseEntity<List<MovimientoReporteDTO>> listarReporteDTO() {
        List<MovimientoReporteDTO> reporte = movimientoServicio.listarReporteDTO();
        return ResponseEntity.ok(reporte);
    }
}
