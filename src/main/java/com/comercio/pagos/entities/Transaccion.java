package com.comercio.pagos.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transacciones", indexes = {
    @Index(name = "idx_id_transaccion", columnList = "id_transaccion"),
    @Index(name = "idx_id_comercio", columnList = "id_comercio"),
    @Index(name = "idx_estado", columnList = "estado"),
    @Index(name = "idx_fecha_creacion", columnList = "fecha_creacion"),
    @Index(name = "idx_id_comercio_estado", columnList = "id_comercio, estado")
})
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transaccion", length = 100, nullable = false, unique = true)
    private String idTransaccion;

    @Column(name = "id_comercio", length = 50, nullable = false)
    private String idComercio;

    @Column(name = "monto", precision = 15, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda;

    @Column(name = "token_tarjeta", length = 255, nullable = false)
    private String tokenTarjeta;

    @Column(name = "fecha_vencimiento", length = 5, nullable = false)
    private String fechaVencimiento;

    @Column(name = "tipo_operacion", length = 20, nullable = false)
    private String tipoOperacion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "codigo_respuesta", length = 10, nullable = false)
    private String codigoRespuesta;

    @Column(name = "mensaje_respuesta", columnDefinition = "TEXT")
    private String mensajeRespuesta;

    @Column(name = "fecha_creacion", nullable = false)
    private Long fechaCreacion;

    @Column(name = "fecha_procesada")
    private Long fechaProcesada;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(String idComercio) {
        this.idComercio = idComercio;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTokenTarjeta() {
        return tokenTarjeta;
    }

    public void setTokenTarjeta(String tokenTarjeta) {
        this.tokenTarjeta = tokenTarjeta;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getFechaProcesada() {
        return fechaProcesada;
    }

    public void setFechaProcesada(Long fechaProcesada) {
        this.fechaProcesada = fechaProcesada;
    }
}
