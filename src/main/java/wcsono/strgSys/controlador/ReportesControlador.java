package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.MovimientoServicio;

@Controller
public class ReportesControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private MovimientoServicio movimientoServicio;

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Inventario actual
        model.addAttribute("articulos", articuloServicio.listarArticulos());

        // Movimientos resumidos para reportes (DTO)
        model.addAttribute("movimientos", movimientoServicio.listarReporteDTO());

        return "Reportes"; // tu plantilla Thymeleaf
    }
}
