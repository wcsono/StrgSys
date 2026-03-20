package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.repositorio.TDocumentoRepositorio;

import java.util.List;

@Service
public class TDocumentoServicio implements ITDocumentoServicio {

    @Autowired
    private TDocumentoRepositorio tdRepositorio;

    @Override
    public List<TipoDocumento> listarTd() {
        return tdRepositorio.findAll();
    }

    @Override
    public TipoDocumento buscarTdporId(Integer idTd) {
        return tdRepositorio.findById(idTd).orElse(null);
    }

    @Override
    public void guardarTd(TipoDocumento td) {
        // Validar si ya existe el código
        if (tdRepositorio.existsByCodTd(td.getCodTd())) {
            throw new IllegalArgumentException("El código ya existe: " + td.getCodTd());
        }
        try {
            tdRepositorio.save(td);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("El código ya existe: " + td.getCodTd());
        }
    }

    @Override
    public void eliminarTd(TipoDocumento td) {
        tdRepositorio.delete(td);
    }

    // Método auxiliar opcional
    public boolean existeCodTd(String codTd) {
        return tdRepositorio.existsByCodTd(codTd);
    }
}