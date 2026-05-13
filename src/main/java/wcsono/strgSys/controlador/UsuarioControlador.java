package wcsono.strgSys.controlador;

import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // Listar usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServicio.listarUsuarios());
        return "usuarios";
    }

    // Mostrar formulario para agregar usuario
    @GetMapping("/agregarUsuario")
    public String mostrarFormularioAgregarUsuario(Model model) {
        Usuario usuario = new Usuario();
        // estUsuario por defecto en 0 (inactivo hasta que se registre en Órdenes)
        usuario.setEstUsuario(0);
        model.addAttribute("usuarioForma", usuario);
        return "agregarUsuario";
    }

    // Guardar nuevo usuario
    @PostMapping("/guardarUsuario")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 RedirectAttributes redirectAttrs) {
        try {
            // Encriptar contraseña
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            usuario.setPassword(encoder.encode(usuario.getPassword()));

            // Por defecto, nuevo usuario queda inactivo (0)
            if (usuario.getEstUsuario() == null) {
                usuario.setEstUsuario(0);
            }

            usuarioServicio.guardarUsuario(usuario);
            redirectAttrs.addFlashAttribute("mensajeExito", "Usuario registrado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al registrar usuario: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }

//    Eliminar Usuario
    @GetMapping("/eliminarUsuario/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer idUsuario, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);

        if (usuario != null) {
            if (usuario.getEstUsuario() != null && usuario.getEstUsuario() == 0) {
                usuarioServicio.eliminarUsuario(idUsuario);
                redirectAttrs.addFlashAttribute("mensajeExito", "Usuario eliminado correctamente.");
            } else {
                redirectAttrs.addFlashAttribute("mensajeError", "No se puede eliminar un usuario activo.");
            }
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Usuario no encontrado.");
        }

        return "redirect:/usuarios";
    }
}
