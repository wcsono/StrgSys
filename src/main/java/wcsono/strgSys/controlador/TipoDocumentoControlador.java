package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wcsono.strgSys.modelo.TipoDocumento;
import wcsono.strgSys.servicio.ITipoDocumentoServicio;

import java.util.List;

@Controller
public class TipoDocumentoControlador {

    private static final Logger logger = LoggerFactory.getLogger(TipoDocumentoControlador.class);

    @Autowired
    private ITipoDocumentoServicio tipoDocumentoService;

    // Listar todos los tipos de documentos
    @GetMapping("/tipoDocumentos")
    public String listar(ModelMap modelo) {
        List<TipoDocumento> tiposDocumentos = tipoDocumentoService.listarTipoDocumentos();
        tiposDocumentos.forEach(td -> logger.info(td.toString()));
        modelo.put("tiposDocumentos", tiposDocumentos);
        modelo.put("tdForma", new TipoDocumento());
        return "tipoDocumentos";
    }

    // Mostrar formulario de agregar
    @GetMapping("/agregarTipoDocumento")
    public String mostrarAgregar() {
        return "agregarTipoDocumento";
    }

    // Guardar nuevo documento con validación de duplicados
    @PostMapping("/guardarTipoDocumento")
    public String guardar(@ModelAttribute("tdForma") TipoDocumento td,
                          RedirectAttributes redirectAttrs) {
        logger.info("TipoDocumento a agregar: {}", td);

        if (tipoDocumentoService.existeCodigo(td.getCodTd())) {
            logger.warn("Intento de insertar codTd duplicado: {}", td.getCodTd());
            redirectAttrs.addFlashAttribute("mensajeError",
                    "El código " + td.getCodTd() + " ya está registrado");
            return "redirect:/tipoDocumentos";
        }

        tipoDocumentoService.guardarTipoDocumento(td);
        redirectAttrs.addFlashAttribute("mensajeExito", "Documento agregado correctamente");
        return "redirect:/tipoDocumentos";
    }

    // Mostrar formulario de edición
    @GetMapping("/editarTd/{id}")
    public String mostrarEditar(@PathVariable("id") Integer idTd,
                                ModelMap modelo,
                                RedirectAttributes redirectAttrs) {
        TipoDocumento tdEditar = tipoDocumentoService.buscarTdPorId(idTd);
        if (tdEditar == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Documento no encontrado");
            return "redirect:/tipoDocumentos";
        }
        logger.info("TipoDocumento a editar: {}", tdEditar);
        modelo.put("tdEditar", tdEditar);
        return "editarTd";
    }

    // Guardar edición con validación de duplicados
    @PostMapping("/guardarEditarTd")
    public String guardarEditar(@ModelAttribute("tdEditar") TipoDocumento tdEditar,
                                RedirectAttributes redirectAttrs) {
        logger.info("TipoDocumento a editar/guardar: {}", tdEditar);

        List<TipoDocumento> existentes = tipoDocumentoService.listarTipoDocumentos();
        boolean duplicado = existentes.stream()
                .anyMatch(doc -> !doc.getIdTd().equals(tdEditar.getIdTd())
                        && doc.getCodTd().equalsIgnoreCase(tdEditar.getCodTd()));

        if (duplicado) {
            logger.warn("Intento de editar con codTd duplicado: {}", tdEditar.getCodTd());
            redirectAttrs.addFlashAttribute("mensajeError",
                    "El código " + tdEditar.getCodTd() + " ya está registrado");
            return "redirect:/tipoDocumentos";
        }

        tipoDocumentoService.guardarTipoDocumento(tdEditar);
        redirectAttrs.addFlashAttribute("mensajeExito", "Documento editado correctamente");
        return "redirect:/tipoDocumentos";
    }

    // Eliminar documento con validación de estado
    @GetMapping("/eliminarTd/{id}")
    public String eliminar(@PathVariable("id") Integer idTd,
                           RedirectAttributes redirectAttrs) {
        TipoDocumento tdEliminar = tipoDocumentoService.buscarTdPorId(idTd);
        logger.info("Resultado buscarTdPorId: {}", tdEliminar);

        if (tdEliminar == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Documento no encontrado");
        } else if (!tdEliminar.isEstTd()) {
            logger.info("TipoDocumento a eliminar: {}", tdEliminar);
            tipoDocumentoService.eliminarTipoDocumento(tdEliminar);
            redirectAttrs.addFlashAttribute("mensajeExito", "Documento eliminado correctamente");
        } else {
            logger.info("No se puede eliminar porque está activo");
            redirectAttrs.addFlashAttribute("mensajeError", "No se puede eliminar el registro porque está activo");
        }
        return "redirect:/tipoDocumentos";
    }

    // Validación AJAX de codTd
    @GetMapping("/validarCodigoTd")
    @ResponseBody
    public boolean validarCodigoTd(@RequestParam String codTd,
                                   @RequestParam(required = false) Integer idTd) {
        TipoDocumento actual = tipoDocumentoService.buscarTdPorId(idTd);

        // Si estamos editando y el código es igual al original, no marcar error
        if (actual != null && actual.getCodTd().equalsIgnoreCase(codTd)) {
            return false;
        }

        // Validar contra los demás registros
        return tipoDocumentoService.listarTipoDocumentos().stream()
                .anyMatch(td -> !td.getIdTd().equals(idTd) &&
                        td.getCodTd().equalsIgnoreCase(codTd));
    }
}