package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wcsono.strgSys.servicio.ArticuloServicio;

@Controller
public class ReportesControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Paso clave: cargar la lista de artículos
        model.addAttribute("articulos", articuloServicio.listarArticulos());
        return "Reportes"; // tu plantilla Thymeleaf
    }

}
