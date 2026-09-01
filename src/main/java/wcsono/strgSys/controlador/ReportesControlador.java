package wcsono.strgSys.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wcsono.strgSys.servicio.ArticuloServicio;
import wcsono.strgSys.servicio.IUsuarioServicio;
import wcsono.strgSys.servicio.MovimientoServicio;
import wcsono.strgSys.servicio.IOrdenServicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReportesControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @Autowired
    private MovimientoServicio movimientoServicio;

    @Autowired
    private IOrdenServicio ordenServicio;

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Inventario actual
        model.addAttribute("articulos", articuloServicio.listarArticulos());

        // Movimientos resumidos para reportes (DTO)
        model.addAttribute("movimientos", movimientoServicio.listarReporteDTO());

        // 👉 Primer card (Total productos)
        model.addAttribute("totalProductos", articuloServicio.contarArticulos());

        // 👉 Tercer card (Valor total inventario)
        Double valorTotalInventario = articuloServicio.obtenerValorTotalInventario();
        System.out.println("💰 Valor total inventario (BE): " + valorTotalInventario);

        model.addAttribute("valorTotalInventario", valorTotalInventario);

        // 👉 Usuarios inactivos
        model.addAttribute("usuariosInactivos", usuarioServicio.contarUsuariosInactivos());

        return "Reportes"; // tu plantilla Thymeleaf
    } // Final de mostrarReportes

    @GetMapping("/reportes/ventas-por-mes")
    @ResponseBody
    public List<Map<String, Object>> obtenerVentasPorMes() {
        List<Object[]> resultados = ordenServicio.obtenerVentasPorMes();
        List<Map<String, Object>> ventasPorMes = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("mes", ((Number) fila[0]).intValue());   // número de mes (1=Enero, 2=Febrero, etc.)
            item.put("ventas", ((Number) fila[1]).intValue());
            ventasPorMes.add(item);
        }

        return ventasPorMes;
    }

    @GetMapping("/reportes/resumen-ordenes")
    @ResponseBody
    public Map<String, Long> obtenerResumenOrdenes() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("cerradasMes", ordenServicio.contarOrdenesCerradasMesActual());
        resumen.put("pendientes", ordenServicio.contarOrdenesPendientes());
        return resumen;
    }

    @GetMapping("/reportes/top-productos-vendidos")
    @ResponseBody
    public List<Map<String, Object>> obtenerTopProductosVendidos() {
        List<Object[]> resultados = ordenServicio.obtenerTopProductosVendidos();
        List<Map<String, Object>> top = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("articulo", (String) fila[0]);
            item.put("ventas", ((Number) fila[1]).intValue());
            top.add(item);
        }

        return top;
    }




}
