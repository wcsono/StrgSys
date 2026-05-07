package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Cliente;
import java.util.List;

public interface IClienteServicio {
    Cliente guardarCliente(Cliente cliente);
    Cliente obtenerClientePorId(Integer idCliente);
    Cliente obtenerClientePorCodCli(String codCli);
    List<Cliente> listarClientes();
    void eliminarCliente(Integer idCliente);
}
