package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.repositorio.ClienteRepositorio;

import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepositorio.findAll();
    }

    @Override
    public Cliente obtenerClientePorId(Integer idCliente) {
        return clienteRepositorio.findById(idCliente).orElse(null);
    }

    @Override
    public Cliente obtenerClientePorCodigo(String codCli) {
        return clienteRepositorio.findByCodCli(codCli).orElse(null); // ✅ implementación
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente); // ✅ devuelve el cliente guardado
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
        clienteRepositorio.deleteById(idCliente);
    }
}
