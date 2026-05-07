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
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    @Override
    public Cliente obtenerClientePorId(Integer idCliente) {
        return clienteRepositorio.findById(idCliente).orElse(null);
    }

    @Override
    public Cliente obtenerClientePorCodCli(String codCli) {
        return clienteRepositorio.findByCodCli(codCli);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepositorio.findAll();
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
        Optional<Cliente> clienteOpt = clienteRepositorio.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            // Validación: si tiene órdenes asociadas, marcar inactivo en lugar de eliminar
            if (cliente.getEstCliente() != null && cliente.getEstCliente() == 0) {
                // cliente inactivo
            } else {
                cliente.setEstCliente(0); // marcar como inactivo
                clienteRepositorio.save(cliente);
            }

        }
    }
}
