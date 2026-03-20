package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.repositorio.TDRepositorio;

import java.util.List;

@Service
public class TDServicio implements ITDServicio{
    @Autowired
    private TDRepositorio tdRepositorio;


    @Override
    public List<TipoDocumento> listarTD() {
        return tdRepositorio.findAll();
    }

    @Override
    public TipoDocumento buscarTDporId(Integer idTd) {
        TipoDocumento td = tdRepositorio.findById(idTd).orElse(null);
        return td;
    }
    @Override
    public void guardarTD(TipoDocumento td) {
        tdRepositorio.save(td);
    }

    @Override
    public void eliminarTD(TipoDocumento td) {
        tdRepositorio.delete(td);
    }
}
