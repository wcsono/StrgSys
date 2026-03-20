package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.TipoDocumento;
import java.util.List;

public interface ITipoDocumentoServicio {

    // Listar todos los tipos de documento
    List<TipoDocumento> listarTipoDocumentos();

    // Buscar un tipo de documento por su ID
    TipoDocumento buscarTdPorId(Integer idTd);

    // Guardar un tipo de documento
    void guardarTipoDocumento(TipoDocumento tipoDocumento);

    // Eliminar un tipo de documento
    void eliminarTipoDocumento(TipoDocumento tipoDocumento);

    // Validar si existe un código de tipo de documento
    boolean existeCodigo(String codTd);
}