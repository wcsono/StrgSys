package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.Cliente;
import wcsono.strgSys.repositorio.ClienteRepositorio;

import java.util.List;
import java.util.Optional;

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
        // Aquí depende de cómo definiste el repositorio:
        // Si tu método devuelve Cliente directamente:
        // return clienteRepositorio.findByCodCli(codCli);
        //
        // Si tu método devuelve Optional<Cliente>:
        return clienteRepositorio.findByCodCli(codCli).orElse(null);
    }

    @Override
    public void guardarCliente(Cliente cliente) {
        if (cliente.getEstCliente() == null) {
            cliente.setEstCliente(0); // por defecto inactivo
        }
        clienteRepositorio.save(cliente);
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
        Optional<Cliente> clienteOpt = clienteRepositorio.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (cliente.getEstCliente() != null && cliente.getEstCliente() == 0) {
                // Si ya está inactivo, eliminar físicamente
                clienteRepositorio.delete(cliente);
            } else {
                // Si está activo, marcar como inactivo
                cliente.setEstCliente(0);
                clienteRepositorio.save(cliente);
            }
        }
    }
}
