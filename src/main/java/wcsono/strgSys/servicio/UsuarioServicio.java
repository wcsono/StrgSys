package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.Usuario;
import wcsono.strgSys.repositorio.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio implements IUsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepositorio.findById(idUsuario).orElse(null);
    }

    @Override
    public Usuario obtenerUsuarioPorUser(String user) {
        return usuarioRepositorio.findByUser(user);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public void eliminarUsuario(Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findById(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Validación: si tiene órdenes asociadas, marcar inactivo en lugar de eliminar
            if (usuario.getEstUsuario() != null && usuario.getEstUsuario() == 0) {
                usuarioRepositorio.delete(usuario);
            } else {
                usuario.setEstUsuario(0); // ✅ marcar como inactivo
                usuarioRepositorio.save(usuario);
            }

        }
    }
    @Override
    public Long contarUsuariosActivos() {
        return usuarioRepositorio.countByEstUsuario(1);
    }

    @Override
    public Long contarUsuariosInactivos() {
        return usuarioRepositorio.countByEstUsuario(0);
    }

}
