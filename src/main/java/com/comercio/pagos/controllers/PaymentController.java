package com.comercio.pagos.controllers;

import com.comercio.pagos.entities.Transaccion;
import com.comercio.pagos.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para procesar pagos y consultar transacciones.
 * este es mi original
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // POST /payments - Procesar un nuevo pago
    @PostMapping
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody PaymentRequest request) {
        try {
            Transaccion transaccion = paymentService.procesarPago(
                request.merchantId,
                request.amount,
                request.currency,
                request.cardToken,
                request.expirationDate,
                request.operationType
            );

            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transaccion.getIdTransaccion());
            response.put("status", transaccion.getEstado());
            response.put("responseCode", transaccion.getCodigoRespuesta());
            response.put("message", transaccion.getMensajeRespuesta());
            response.put("timestamp", transaccion.getFechaProcesada());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error procesando pago");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /payments/{transactionId} - Consultar una transacción
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> obtenerTransaccion(@PathVariable String transactionId) {
        try {
            Transaccion transaccion = paymentService.obtenerTransaccionPorId(transactionId);
            return ResponseEntity.ok(transaccion);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Transacción no encontrada");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // GET /payments?merchantId=...&status=... - Listar transacciones con filtros
    @GetMapping
    public ResponseEntity<List<Transaccion>> listarTransacciones(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String status) {
        
        List<Transaccion> transacciones = paymentService.listarTransacciones(merchantId, status);
        return ResponseEntity.ok(transacciones);
    }

    // Clase interna para recibir datos del request
    static class PaymentRequest {
        public String merchantId;        // ID del comercio
        public BigDecimal amount;        // Monto
        public String currency;          // Moneda (USD, PEN, etc.)
        public String cardToken;         // Token o PAN hasheado
        public String expirationDate;    // Fecha vencimiento (MM/YY)
        public String operationType;     // Tipo operación (COMPRA)
    }
}
