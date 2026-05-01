package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wcsono.strgSys.modelo.Articulo;

import java.util.List;

public interface ArticuloRepositorio extends JpaRepository<Articulo, Integer> {

    // Validación de existencia por código
    boolean existsByCodArt(String codArt);

    // Filtros por código y descripción (consulta dinámica)
    @Query("SELECT a FROM Articulo a " +
            "WHERE (:codArt IS NULL OR UPPER(a.codArt) LIKE UPPER(CONCAT('%', :codArt, '%'))) " +
            "AND (:desArt IS NULL OR UPPER(a.desArt) LIKE UPPER(CONCAT('%', :desArt, '%')))")
    List<Articulo> findByFiltros(@Param("codArt") String codArt,
                                 @Param("desArt") String desArt);
}
