package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.TipoDocumento;

public interface TDRepositorio extends JpaRepository<TipoDocumento, Integer> {
}
