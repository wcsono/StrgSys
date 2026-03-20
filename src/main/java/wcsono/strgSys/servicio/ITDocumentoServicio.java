package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.TipoDocumento;
import java.util.List;

public interface ITDocumentoServicio {

    // Listar todos los tipos de documentos
    List<TipoDocumento> listarTd();

    // Buscar un tipo de documento por su ID
    TipoDocumento buscarTdporId(Integer idTd);

    // Guardar un tipo de documento
    void guardarTd(TipoDocumento td);

    // Eliminar un tipo de documento
    void eliminarTd(TipoDocumento td);
}