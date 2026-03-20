package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.repositorio.TipoDocumentoRepositorio;

import java.util.List;

@Service
public class TipoDocumentoServicioImpl implements ITipoDocumentoServicio {

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;

    @Override
    public List<TipoDocumento> listarTipoDocumentos() {
        return tipoDocumentoRepositorio.findAll();
    }

    @Override
    public TipoDocumento buscarTdPorId(Integer idTd) {
        return tipoDocumentoRepositorio.findById(idTd).orElse(null);
    }

    @Override
    public void guardarTipoDocumento(TipoDocumento tipoDocumento) {
        tipoDocumentoRepositorio.save(tipoDocumento);
    }

    @Override
    public void eliminarTipoDocumento(TipoDocumento tipoDocumento) {
        tipoDocumentoRepositorio.delete(tipoDocumento);
    }

    @Override
    public boolean existeCodigo(String codTd) {
        return tipoDocumentoRepositorio.existsByCodTd(codTd.toUpperCase());
    }
}