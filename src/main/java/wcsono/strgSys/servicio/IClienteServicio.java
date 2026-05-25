package wcsono.strgSys.servicio;

import wcsono.strgSys.modelo.Cliente;
import java.util.List;

public interface IClienteServicio {
    List<Cliente> listarClientes();
    Cliente obtenerClientePorId(Integer idCliente);
    Cliente obtenerClientePorCodigo(String codCli); // ✅ nombre uniforme
    Cliente guardarCliente(Cliente cliente);        // ✅ devolver Cliente guardado
    void eliminarCliente(Integer idCliente);
}
