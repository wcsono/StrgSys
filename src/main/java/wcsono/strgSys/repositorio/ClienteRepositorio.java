package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Cliente findByCodCli(String codCli);
}
