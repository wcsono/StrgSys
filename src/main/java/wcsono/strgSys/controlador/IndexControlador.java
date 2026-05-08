package wcsono.strgSys.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexControlador {

    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {
        // Recuperar el usuario en sesión
        Object usuarioSesion = session.getAttribute("usuarioSesion");

        // Pasar el usuario al modelo (opcional, si quieres usarlo en la vista)
        model.addAttribute("usuarioSesion", usuarioSesion);

        // Retorna la plantilla index.html
        return "index";
    }
}
