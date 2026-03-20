package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.TipoDocumento;

public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Integer> {
    boolean existsByCodTd(String codTd);
}