package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wcsono.strgSys.modelo.DetalleOrden;

import java.util.List;

@Repository
public interface DetalleOrdenRepositorio extends JpaRepository<DetalleOrden, Integer> {

    // 🔹 Listar todos los detalles de una orden específica
    List<DetalleOrden> findByOrdenIdOrd(Integer idOrd);

    // (Opcional) Si quieres traer todos los detalles activos de una orden,
    // podrías añadir un método con filtro adicional, por ejemplo:
    // List<DetalleOrden> findByOrdenIdOrdAndOrdenEstOrd(Integer idOrd, Integer estOrd);
}
