package wcsono.strgSys.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wcsono.strgSys.modelo.Usuario;

@Controller
public class IndexControlador {

    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioSesion == null) {
            // Sesión expirada o no iniciada → redirige al login
            return "redirect:/login";
        }

        // Pasar usuario al modelo para mostrar en la vista
        model.addAttribute("usuarioSesion", usuarioSesion);
        return "index";
    }
}
