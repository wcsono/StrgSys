package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Usuario findByUser(String user);
}
