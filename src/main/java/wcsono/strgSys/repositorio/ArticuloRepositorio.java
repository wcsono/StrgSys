package wcsono.strgSys.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import wcsono.strgSys.modelo.Articulo;

public interface ArticuloRepositorio extends JpaRepository<Articulo, Integer> {
    boolean existsByCodArt(String codArt);
}