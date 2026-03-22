package wcsono.strgSys.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wcsono.strgSys.modelo.Articulo;
import wcsono.strgSys.servicio.ArticuloServicio;

import java.util.List;
import java.util.Optional;

@Controller
public class ArticuloControlador {

    private static final Logger logger = LoggerFactory.getLogger(ArticuloControlador.class);
    private final ArticuloServicio articuloServicio;

    // Constructor injection
    public ArticuloControlador(ArticuloServicio articuloServicio) {
        this.articuloServicio = articuloServicio;
    }

    @GetMapping("/articulos")
    public String iniciar(ModelMap modelo) {
        List<Articulo> articulos = articuloServicio.listarArticulos();
        modelo.put("articulos", articulos);
        modelo.put("articuloForma", new Articulo());
        return "Articulos";
    }
    @GetMapping("/agregarArt")
    public String abrirAgregar(){
        return "agregarArt";
    }

    @PostMapping("/guardarAgregarArt")
    public String agregar(@ModelAttribute("articuloForma") Articulo articulo,
                          RedirectAttributes redirectAttrs) {
        logger.info("Artículo a agregar: {}", articulo);

        // Validar unicidad de codArt
        boolean existe = articuloServicio.listarArticulos().stream()
                .anyMatch(a -> a.getCodArt().equalsIgnoreCase(articulo.getCodArt()));

        if (existe) {
            logger.warn("Ya existe un artículo con codArt: {}", articulo.getCodArt());
            redirectAttrs.addFlashAttribute("mensajeError",
                    "El código de artículo '" + articulo.getCodArt() + "' ya está registrado.");
            return "redirect:/articulos";
        }

        articuloServicio.guardarArticulo(articulo);
        redirectAttrs.addFlashAttribute("mensajeExito", "Artículo agregado correctamente.");
        return "redirect:/articulos";
    }

    @GetMapping("/editarArt/{id}")
    public String editarArticulo(@PathVariable("id") int idArticulo, ModelMap modelo) {
        Articulo artEditar = articuloServicio.buscarArticuloPorId(idArticulo);
        if (artEditar == null) {
            logger.warn("Artículo no encontrado con ID: {}", idArticulo);
            return "fragmentos/error :: mensajeError";
        }
        modelo.put("artEditar", artEditar); // nombre debe coincidir con th:object
        return "/editarArt";
    }

    @PostMapping("/guardarEditarArt")
    public String guardarEditar(@ModelAttribute("artEditar") Articulo articulo,
                         RedirectAttributes redirectAttrs) {
        logger.info("Artículo a editar: {}", articulo);

        // Validar unicidad de codArt (permitiendo el mismo codArt en el mismo artículo)
        boolean existe = articuloServicio.listarArticulos().stream()
                .anyMatch(a -> !a.getIdArt().equals(articulo.getIdArt()) &&
                        a.getCodArt().equalsIgnoreCase(articulo.getCodArt()));

        if (existe) {
            logger.warn("Intento de duplicar codArt en edición: {}", articulo.getCodArt());
            redirectAttrs.addFlashAttribute("mensajeError",
                    "El código de artículo '" + articulo.getCodArt() + "' ya está registrado en otro artículo.");
            return "redirect:/articulos";
        }

        articuloServicio.guardarArticulo(articulo);
        redirectAttrs.addFlashAttribute("mensajeExito", "Artículo editado correctamente.");
        return "redirect:/articulos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarArticulo(@PathVariable("id") int idArticulo,
                                   RedirectAttributes redirectAttrs) {
        Articulo articulo = articuloServicio.buscarArticuloPorId(idArticulo);
        if (articulo != null && articulo.getStk() == 0) {
            articuloServicio.eliminarArticulo(articulo);
            logger.info("Artículo eliminado: {}", articulo);
            redirectAttrs.addFlashAttribute("mensajeExito", "Artículo eliminado correctamente.");
        } else {
            logger.warn("No se puede eliminar el artículo con stock > 0: {}", idArticulo);
            redirectAttrs.addFlashAttribute("mensajeError",
                    "No se puede eliminar el artículo mientras el stock sea mayor a 0.");
        }
        return "redirect:/articulos";
    }
    @GetMapping("/validarCodigoArt")
    @ResponseBody
    public boolean validarCodigoArt(@RequestParam String codArt,
                                    @RequestParam(required = false) Integer idArt) {
        // Buscar el artículo actual
        Optional<Articulo> articuloActual = articuloServicio.listarArticulos().stream()
                .filter(a -> a.getIdArt().equals(idArt))
                .findFirst();

        // Si estamos editando y el código es igual al original, no validar
        if (articuloActual.isPresent() && articuloActual.get().getCodArt().equalsIgnoreCase(codArt)) {
            return false; // No hay error
        }

        // Validar contra los demás artículos
        return articuloServicio.listarArticulos().stream()
                .anyMatch(a -> !a.getIdArt().equals(idArt) &&
                        a.getCodArt().equalsIgnoreCase(codArt));
    }

}