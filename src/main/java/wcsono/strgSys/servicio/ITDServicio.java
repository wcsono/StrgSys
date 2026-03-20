package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.TipoDocumento;

import java.util.List;

public interface ITDServicio {

    public List<TipoDocumento> listarTD();

    public TipoDocumento buscarTDporId(Integer idTd);

    public  void guardarTD(TipoDocumento td);

    public void eliminarTD(TipoDocumento td);

}
