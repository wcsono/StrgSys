package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import wcsono.strgSys.enums.EstadoFactura;

import java.time.LocalDateTime;

@Entity
@Table(name = "facturacion")
public class Facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFact;

    @Column(name = "num_factura", nullable = false, unique = true)
    private String numFactura;

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado;

    @Column(name = "fecha_facturacion", nullable = false)
    private LocalDateTime fechaFacturacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ord", nullable = false)
    private Orden orden;

    // Getters y Setters
    public Long getIdFact() {
        return idFact;
    }

    public void setIdFact(Long idFact) {
        this.idFact = idFact;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaFacturacion() {
        return fechaFacturacion;
    }

    public void setFechaFacturacion(LocalDateTime fechaFacturacion) {
        this.fechaFacturacion = fechaFacturacion;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }
}
