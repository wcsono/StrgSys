package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.TipoDocumento;

public interface TDocumentoRepositorio extends JpaRepository<TipoDocumento, Integer> {

    // Verifica si existe un código de documento
    boolean existsByCodTd(String codTd);
}