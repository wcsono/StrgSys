package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.modelo.TipoMovimiento;
import wcsono.strgSys.modelo.SubTipoMovimiento;

import java.util.List;

public interface ITipoDocumentoServicio {

    // Listar todos los tipos de documento
    List<TipoDocumento> listarTipoDocumentos();

    // Buscar un tipo de documento por su ID
    TipoDocumento buscarTdPorId(Integer idTd);

    // Guardar un tipo de documento
    void guardarTipoDocumento(TipoDocumento tipoDocumento);

    // Inactivar un tipo de documento (en lugar de eliminar físico)
    void eliminarTipoDocumento(TipoDocumento tipoDocumento);

    // Validar si existe un código de tipo de documento
    boolean existeCodigo(String codTd);

    // Listar solo los activos
    List<TipoDocumento> listarActivos();

    // Listar por tipo de movimiento
    List<TipoDocumento> listarPorTipoMovimiento(TipoMovimiento tipoMovimiento);

    // Listar por subtipo de movimiento
    List<TipoDocumento> listarPorSubTipoMovimiento(SubTipoMovimiento subTipoMovimiento);

    // Eliminar físicamente un documento (solo si está inactivo)
    void borrarFisico(TipoDocumento tipoDocumento);
}
