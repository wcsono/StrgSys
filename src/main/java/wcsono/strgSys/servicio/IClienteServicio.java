package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Cliente;
import java.util.List;

public interface IClienteServicio {
    List<Cliente> listarClientes();
    Cliente obtenerClientePorId(Integer idCliente);
    Cliente obtenerClientePorCodigo(String codCli);
    void guardarCliente(Cliente cliente);
    void eliminarCliente(Integer idCliente);
}
