package wcsono.strgSys.enums;

public enum EstadoOrden {
    INICIAL(0, "Inicial", "badge bg-secondary"),
    ABIERTA(1, "Abierta", "badge bg-info"),
    FACTURADA(2, "Facturada", "badge bg-primary"),
    PREPARACION(3, "En preparación", "badge bg-warning"),
    ENTREGADA(4, "Entregada", "badge bg-success"),
    INGRESADA(5, "Ingresada", "badge bg-dark"),
    EXTORNADA(6, "Extornada", "badge bg-warning"),
    DEVUELTA(7, "Devuelta", "badge bg-danger"),
    CERRADA(8, "Cerrada", "badge bg-success"),
    ANULADA(9, "Anulada", "badge bg-danger");

    private final int codigo;
    private final String descripcion;
    private final String cssClass;

    EstadoOrden(int codigo, String descripcion, String cssClass) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cssClass = cssClass;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCssClass() {
        return cssClass;
    }

    /** Devuelve la clase CSS para la fila <tr> */
    public String getRowClass() {
        return "tr-" + this.name().toLowerCase();
    }

    public static EstadoOrden fromCodigo(int codigo) {
        for (EstadoOrden estado : values()) {
            if (estado.codigo == codigo) {
                return estado;
            }
        }
        return null;
    }
}
