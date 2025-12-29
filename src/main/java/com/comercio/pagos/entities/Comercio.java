package com.comercio.pagos.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "comercios", indexes = {
    @Index(name = "idx_id_comercio", columnList = "id_comercio"),
    @Index(name = "idx_estado", columnList = "estado")
})
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_comercio", length = 50, nullable = false, unique = true)
    private String idComercio;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "monto_maximo_transaccion", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoMaximoTransaccion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false)
    private Long fechaCreacion;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(String idComercio) {
        this.idComercio = idComercio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getMontoMaximoTransaccion() {
        return montoMaximoTransaccion;
    }

    public void setMontoMaximoTransaccion(BigDecimal montoMaximoTransaccion) {
        this.montoMaximoTransaccion = montoMaximoTransaccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
