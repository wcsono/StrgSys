package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wcsono.strgSys.modelo.DetalleOrden;

import java.util.List;

@Repository
public interface DetalleOrdenRepositorio extends JpaRepository<DetalleOrden, Integer> {

    // 🔹 Método para listar todos los detalles de una orden específica
    List<DetalleOrden> findByOrdenIdOrd(Integer idOrd);
}