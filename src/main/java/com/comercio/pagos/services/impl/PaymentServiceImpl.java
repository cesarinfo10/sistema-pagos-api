package com.comercio.pagos.services.impl;

import com.comercio.pagos.entities.Comercio;
import com.comercio.pagos.entities.Transaccion;
import com.comercio.pagos.repositories.ComercioRepository;
import com.comercio.pagos.repositories.TransaccionRepository;
import com.comercio.pagos.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Implementación del servicio de procesamiento de pagos.
 * Gestiona el flujo completo de transacciones desde la validación hasta la persistencia.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final TransaccionRepository transaccionRepository;
    private final ComercioRepository comercioRepository;
    private final Random random = new Random();

    @Value("${pagos.tarjeta.patron-rechazo:^4111111111111111$}")
    private String patronTarjetaRechazo;

    public PaymentServiceImpl(TransaccionRepository transaccionRepository, 
                             ComercioRepository comercioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.comercioRepository = comercioRepository;
    }

    @Override
    public Transaccion procesarPago(String idComercio, BigDecimal monto, String moneda,
                                   String tokenTarjeta, String fechaVencimiento, String tipoOperacion) {
        
        String idTransaccion = generarIdTransaccion();
        log.info("[{}] Iniciando procesamiento de pago - Comercio: {}, Monto: {} {}, Tipo: {}", 
                 idTransaccion, idComercio, monto, moneda, tipoOperacion);

        Transaccion transaccion = new Transaccion();
        transaccion.setIdTransaccion(idTransaccion);
        transaccion.setIdComercio(idComercio);
        transaccion.setMonto(monto);
        transaccion.setMoneda(moneda);
        transaccion.setTokenTarjeta(tokenTarjeta);
        transaccion.setFechaVencimiento(fechaVencimiento);
        transaccion.setTipoOperacion(tipoOperacion);
        transaccion.setFechaCreacion(System.currentTimeMillis());
        transaccion.setEstado("PENDIENTE");

        try {
            // 1. Validar que el comercio existe y está activo
            log.debug("[{}] Validando comercio: {}", idTransaccion, idComercio);
            Comercio comercio = validarComercio(idComercio);
            
            // 2. Validar reglas de negocio: límite de monto
            log.debug("[{}] Validando límite de monto. Límite: {}, Monto: {}", 
                     idTransaccion, comercio.getMontoMaximoTransaccion(), monto);
            if (monto.compareTo(comercio.getMontoMaximoTransaccion()) > 0) {
                log.warn("[{}] Transacción rechazada: monto {} excede límite {}", 
                        idTransaccion, monto, comercio.getMontoMaximoTransaccion());
                transaccion.setEstado("DECLINED");
                transaccion.setCodigoRespuesta("E001");
                transaccion.setMensajeRespuesta("Monto excede el límite permitido para el comercio");
                transaccion.setFechaProcesada(System.currentTimeMillis());
                return transaccionRepository.save(transaccion);
            }

            // 3. Validar tarjeta simulada (patrón configurable)
            log.debug("[{}] Validando tarjeta contra patrón de rechazo", idTransaccion);
            if (esTarjetaSimulada(tokenTarjeta)) {
                log.warn("[{}] Transacción rechazada: tarjeta simulada detectada", idTransaccion);
                transaccion.setEstado("DECLINED");
                transaccion.setCodigoRespuesta("E002");
                transaccion.setMensajeRespuesta("Tarjeta no permitida");
                transaccion.setFechaProcesada(System.currentTimeMillis());
                return transaccionRepository.save(transaccion);
            }

            // 4. Llamar al issuer mock
            log.debug("[{}] Consultando issuer mock", idTransaccion);
            IssuerResponse issuerResponse = consultarIssuerMock(idTransaccion, monto, tokenTarjeta);
            
            // 5. Procesar respuesta del issuer
            transaccion.setEstado(issuerResponse.aprobado ? "APPROVED" : "DECLINED");
            transaccion.setCodigoRespuesta(issuerResponse.codigoRespuesta);
            transaccion.setMensajeRespuesta(issuerResponse.mensaje);
            transaccion.setFechaProcesada(System.currentTimeMillis());

            log.info("[{}] Transacción procesada exitosamente - Estado: {}, Código: {}", 
                    idTransaccion, transaccion.getEstado(), transaccion.getCodigoRespuesta());

        } catch (Exception e) {
            log.error("[{}] Error procesando transacción: {}", idTransaccion, e.getMessage(), e);
            transaccion.setEstado("ERROR");
            transaccion.setCodigoRespuesta("E999");
            transaccion.setMensajeRespuesta("Error interno del sistema: " + e.getMessage());
            transaccion.setFechaProcesada(System.currentTimeMillis());
        }

        return transaccionRepository.save(transaccion);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaccion obtenerTransaccionPorId(String transactionId) {
        log.info("Consultando transacción: {}", transactionId);
        return transaccionRepository.findByIdTransaccion(transactionId)
                .orElseThrow(() -> {
                    log.error("Transacción no encontrada: {}", transactionId);
                    return new RuntimeException("Transacción no encontrada: " + transactionId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaccion> listarTransacciones(String merchantId, String status) {
        log.info("Listando transacciones - Comercio: {}, Estado: {}", merchantId, status);
        
        if (merchantId != null && status != null) {
            return transaccionRepository.findByIdComercioAndEstado(merchantId, status);
        } else if (merchantId != null) {
            return transaccionRepository.findByIdComercio(merchantId);
        } else if (status != null) {
            return transaccionRepository.findByEstado(status);
        } else {
            return transaccionRepository.findAll();
        }
    }

    /**
     * Valida que el comercio existe y está activo.
     */
    private Comercio validarComercio(String idComercio) {
        Comercio comercio = comercioRepository.findByIdComercio(idComercio)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado: " + idComercio));
        
        if (!"ACTIVO".equals(comercio.getEstado())) {
            throw new RuntimeException("Comercio inactivo: " + idComercio);
        }
        
        return comercio;
    }

    /**
     * Verifica si la tarjeta coincide con el patrón de tarjetas simuladas a rechazar.
     */
    private boolean esTarjetaSimulada(String tokenTarjeta) {
        return tokenTarjeta.matches(patronTarjetaRechazo);
    }

    /**
     * Simula la consulta a un issuer bancario.
     * En producción, esto sería una llamada HTTP a un servicio externo.
     */
    private IssuerResponse consultarIssuerMock(String idTransaccion, BigDecimal monto, String tokenTarjeta) {
        log.debug("[{}] Issuer Mock - Procesando autorización", idTransaccion);
        
        // Simular procesamiento aleatorio (70% aprobado, 30% rechazado)
        boolean aprobado = random.nextInt(100) < 70;
        
        IssuerResponse response = new IssuerResponse();
        response.aprobado = aprobado;
        
        if (aprobado) {
            response.codigoRespuesta = "00";
            response.mensaje = "Transacción aprobada";
            log.debug("[{}] Issuer Mock - APROBADO", idTransaccion);
        } else {
            response.codigoRespuesta = "05";
            response.mensaje = "Transacción rechazada por el banco emisor";
            log.debug("[{}] Issuer Mock - RECHAZADO", idTransaccion);
        }
        
        return response;
    }

    /**
     * Genera un ID único para la transacción.
     */
    private String generarIdTransaccion() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 18).toUpperCase();
    }

    /**
     * Clase interna para representar la respuesta del issuer mock.
     */
    private static class IssuerResponse {
        boolean aprobado;
        String codigoRespuesta;
        String mensaje;
    }
}
