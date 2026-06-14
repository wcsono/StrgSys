package wcsono.strgSys.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.enums.TipoMovimiento;
import wcsono.strgSys.enums.SubTipoMovimiento;

public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Integer> {

    // Verifica si existe un código de documento
    boolean existsByCodTd(String codTd);

    // Buscar por código
    TipoDocumento findByCodTd(String codTd);

    // Listar solo los activos
    List<TipoDocumento> findByEstTdTrue();

    // Listar por tipo de movimiento (INGRESO / SALIDA)
    List<TipoDocumento> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    // Listar por subtipo de movimiento
    List<TipoDocumento> findBySubTipoMovimiento(SubTipoMovimiento subTipoMovimiento);
}
