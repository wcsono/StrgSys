package wcsono.strgSys.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 🔹 Import necesario
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.IUsuarioServicio;

@Controller
public class LoginControlador {

    @Autowired
    private IUsuarioServicio usuarioServicio;

    // 🔹 Redirige siempre al login al entrar al sistema
    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    // 🔹 Muestra la página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // 🔹 Procesa el formulario de login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String user,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioServicio.obtenerUsuarioPorUser(user);

        if (usuario != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // 🔹 Validar: texto plano (solo para pruebas) o encriptado
            boolean passwordValida = usuario.getPassword().equals(password)
                    || encoder.matches(password, usuario.getPassword());

            if (passwordValida) {
                // Guardar usuario en sesión
                session.setAttribute("usuarioSesion", usuario);
                redirectAttrs.addFlashAttribute("mensaje", "Bienvenido " + usuario.getNombre());
                return "redirect:/index"; // menú principal
            } else {
                redirectAttrs.addFlashAttribute("error", "Contraseña incorrecta.");
                return "redirect:/login";
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }
    }

    // 🔹 Logout manual (invalida sesión)
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
