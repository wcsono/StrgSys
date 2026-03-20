package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.DetalleOrden;
import wcsono.strgSys.repositorio.DetalleOrdenRepositorio;

import java.util.List;

@Service
public class DetalleOrdenServicio implements IDetalleOrdenServicio {

    @Autowired
    private DetalleOrdenRepositorio detalleOrdenRepositorio;

    @Override
    public List<DetalleOrden> listarDetalleOrden() {
        return detalleOrdenRepositorio.findAll();
    }

    @Override
    public DetalleOrden buscarDetalleOrdenPorId(Integer idDo) {
        return detalleOrdenRepositorio.findById(idDo).orElse(null);
    }

    @Override
    public void guardarDetalleOrden(DetalleOrden detalleOrden) {
        if (detalleOrden.getOrden() == null) {
            throw new IllegalArgumentException("La orden no puede ser nula al guardar un detalle");
        }
        if (detalleOrden.getArticulo() == null) {
            throw new IllegalArgumentException("El artículo no puede ser nulo al guardar un detalle");
        }
        detalleOrdenRepositorio.save(detalleOrden);
    }

    @Override
    public void eliminarDetalleOrden(DetalleOrden detalleOrden) {
        detalleOrdenRepositorio.delete(detalleOrden);
    }

    @Override
    public List<DetalleOrden> listarPorOrden(Integer idOrd) {
        return detalleOrdenRepositorio.findByOrdenIdOrd(idOrd);
    }

    // 🔹 Método adicional para compatibilidad con tu controlador
    public DetalleOrden guardar(DetalleOrden detalleOrden) {
        guardarDetalleOrden(detalleOrden);
        return detalleOrden;
    }
}