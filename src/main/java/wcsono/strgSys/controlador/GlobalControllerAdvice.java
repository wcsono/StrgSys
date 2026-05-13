package wcsono.strgSys.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUsuarioSesion(Model model, HttpSession session) {
        // Recupera el usuario de la sesión (si existe)
        Object usuarioSesion = session.getAttribute("usuarioSesion");

        // Lo agrega al modelo para que esté disponible en todas las vistas
        model.addAttribute("usuarioSesion", usuarioSesion);
    }
}
