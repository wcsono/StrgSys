package wcsono.strgSys.dto;

public class MovimientoDTO {
    private String articulo;
    private int anio;
    private int mes;
    private String tipo;
    private int cantidad;
    private double costo;

    // Constructor
    public MovimientoDTO(String articulo, int anio, int mes,
                         String tipo, int cantidad, double costo) {
        this.articulo = articulo;
        this.anio = anio;
        this.mes = mes;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.costo = costo;
    }

    // Getters y Setters
    public String getArticulo() { return articulo; }
    public void setArticulo(String articulo) { this.articulo = articulo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
}
