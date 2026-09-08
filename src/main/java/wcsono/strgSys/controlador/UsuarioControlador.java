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

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // 📌 Listar usuarios → ADMIN y OPERADOR, ALMACÉN no accede
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        // Caso 1: No hay sesión → al login
        if (usuarioActivo == null) {
            model.addAttribute("mensajeError", "Debe iniciar sesión para continuar.");
            return "login";  // ✅ carga la vista login.html
        }

        // Caso 2: Usuario Almacén → quedarse en index con alerta
        if (usuarioActivo.getNivelAcceso() == 3) { // 3 = Almacén
            model.addAttribute("mensajeError", "No tiene permiso para acceder a esta página");
            return "index";  // ✅ vuelve a la vista index.html
        }

        // Caso 3: ADMIN y OPERADOR → acceso normal
        model.addAttribute("usuarios", usuarioServicio.listarUsuarios());
        return "usuarios";  // ✅ carga usuarios.html
    }

    // 📌 Agregar usuario → solo ADMIN
    @GetMapping("/agregarUsuario")
    public String mostrarFormularioAgregarUsuario(Model model, HttpSession session, RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) { // 1 = Admin
            redirectAttrs.addFlashAttribute("mensajeError", "No tiene Permiso para realizar este proceso");
            return "redirect:/usuarios";
        }

        Usuario usuario = new Usuario();
        usuario.setEstUsuario(0); // inactivo por defecto
        model.addAttribute("usuarioForma", usuario);
        return "agregarUsuario";
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 HttpSession session,
                                 RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) {
            redirectAttrs.addFlashAttribute("mensajeError", "No tiene Permiso para realizar este proceso");
            return "redirect:/usuarios";
        }

        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            usuario.setPassword(encoder.encode(usuario.getPassword()));

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

    // 📌 Eliminar usuario → solo ADMIN, y solo si está inactivo
    @GetMapping("/eliminarUsuario/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer idUsuario,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) {
            redirectAttrs.addFlashAttribute("mensajeError", "No tiene Permiso para realizar este proceso");
            return "redirect:/usuarios";
        }

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

    // 📌 Editar usuario → solo ADMIN
    @GetMapping("/editarUsuario/{id}")
    public String mostrarEditarUsuario(@PathVariable("id") Integer idUsuario,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) {
            redirectAttrs.addFlashAttribute("mensajeError", "No tiene Permiso para realizar este proceso");
            return "redirect:/usuarios";
        }

        Usuario usuarioEditar = usuarioServicio.obtenerUsuarioPorId(idUsuario);

        if (usuarioEditar != null) {
            model.addAttribute("usuarioEditar", usuarioEditar);
            return "editarUsuario"; // vista Thymeleaf
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/guardarEditarUsuario")
    public String guardarEditarUsuario(@ModelAttribute Usuario usuario,
                                       HttpSession session,
                                       RedirectAttributes redirectAttrs) {
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioActivo == null || usuarioActivo.getNivelAcceso() != 1) {
            redirectAttrs.addFlashAttribute("mensajeError", "No tiene Permiso para realizar este proceso");
            return "redirect:/usuarios";
        }

        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            usuario.setPassword(encoder.encode(usuario.getPassword()));

            usuarioServicio.guardarUsuario(usuario);
            redirectAttrs.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al actualizar usuario: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }
}
