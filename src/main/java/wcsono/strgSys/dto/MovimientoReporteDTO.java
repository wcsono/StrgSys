package wcsono.strgSys.dto;

public class MovimientoReporteDTO {
    private String articulo;
    private int anio;
    private int mes;
    private int entradas;
    private int salidas;
    private int saldoFinal;
    private double valorMovido;

    // Constructor
    public MovimientoReporteDTO(String articulo, int anio, int mes,
                                int entradas, int salidas, int saldoFinal, double valorMovido) {
        this.articulo = articulo;
        this.anio = anio;
        this.mes = mes;
        this.entradas = entradas;
        this.salidas = salidas;
        this.saldoFinal = saldoFinal;
        this.valorMovido = valorMovido;
    }

    // Getters y Setters
    public String getArticulo() { return articulo; }
    public void setArticulo(String articulo) { this.articulo = articulo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getEntradas() { return entradas; }
    public void setEntradas(int entradas) { this.entradas = entradas; }

    public int getSalidas() { return salidas; }
    public void setSalidas(int salidas) { this.salidas = salidas; }

    public int getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(int saldoFinal) { this.saldoFinal = saldoFinal; }

    public double getValorMovido() { return valorMovido; }
    public void setValorMovido(double valorMovido) { this.valorMovido = valorMovido; }
}
