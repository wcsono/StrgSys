package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wcsono.strgSys.modelo.Cliente;

import java.util.Optional;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCodCli(String codCli); // ✅ método para buscar por código
}
