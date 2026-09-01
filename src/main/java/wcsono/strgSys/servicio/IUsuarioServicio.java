package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Usuario;
import java.util.List;

public interface IUsuarioServicio {
    Usuario guardarUsuario(Usuario usuario);
    Usuario obtenerUsuarioPorId(Integer idUsuario);
    Usuario obtenerUsuarioPorUser(String user);
    List<Usuario> listarUsuarios();
    void eliminarUsuario(Integer idUsuario);
    Long contarUsuariosActivos();
    Long contarUsuariosInactivos();
}
