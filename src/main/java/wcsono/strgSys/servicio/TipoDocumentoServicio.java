package wcsono.strgSys.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.modelo.TipoMovimiento;
import wcsono.strgSys.modelo.SubTipoMovimiento;
import wcsono.strgSys.repositorio.TipoDocumentoRepositorio;

import java.util.List;

@Service
public class TipoDocumentoServicio implements ITipoDocumentoServicio {

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;

    @Override
    public List<TipoDocumento> listarTipoDocumentos() {
        return tipoDocumentoRepositorio.findAll();
    }

    @Override
    public TipoDocumento buscarTdPorId(Integer idTd) {
        return tipoDocumentoRepositorio.findById(idTd).orElse(null);
    }

    @Override
    public void guardarTipoDocumento(TipoDocumento tipoDocumento) {
        try {
            // Validación de duplicados solo en inserción nueva
            if (tipoDocumento.getIdTd() == null && tipoDocumentoRepositorio.existsByCodTd(tipoDocumento.getCodTd())) {
                throw new IllegalArgumentException("El código ya existe: " + tipoDocumento.getCodTd());
            }
            tipoDocumentoRepositorio.save(tipoDocumento);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error al guardar, código duplicado: " + tipoDocumento.getCodTd());
        }
    }

    @Override
    public void eliminarTipoDocumento(TipoDocumento tipoDocumento) {
        // En lugar de borrar, marcamos como inactivo
        tipoDocumento.setEstTd(false);
        tipoDocumentoRepositorio.save(tipoDocumento);
    }

    @Override
    public boolean existeCodigo(String codTd) {
        return tipoDocumentoRepositorio.existsByCodTd(codTd.toUpperCase());
    }

    @Override
    public List<TipoDocumento> listarActivos() {
        return tipoDocumentoRepositorio.findByEstTdTrue();
    }

    @Override
    public List<TipoDocumento> listarPorTipoMovimiento(TipoMovimiento tipoMovimiento) {
        return tipoDocumentoRepositorio.findByTipoMovimiento(tipoMovimiento);
    }

    @Override
    public List<TipoDocumento> listarPorSubTipoMovimiento(SubTipoMovimiento subTipoMovimiento) {
        return tipoDocumentoRepositorio.findBySubTipoMovimiento(subTipoMovimiento);
    }

    @Override
    public void borrarFisico(TipoDocumento tipoDocumento) {
        // Eliminación física solo si está inactivo
        if (!tipoDocumento.isEstTd()) {
            tipoDocumentoRepositorio.delete(tipoDocumento);
        } else {
            throw new IllegalArgumentException("No se puede eliminar: el documento está activo en órdenes");
        }
    }
}
