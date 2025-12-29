package com.comercio.pagos.services;

import com.comercio.pagos.entities.Transaccion;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para procesar y gestionar transacciones de pago.
 */
public interface PaymentService {

    /**
     * Procesa una nueva transacción de pago.
     *
     * @param idComercio ID del comercio que realiza la transacción
     * @param monto Monto de la transacción
     * @param moneda Moneda de la transacción (ej: USD, PEN, EUR)
     * @param tokenTarjeta Token o PAN hasheado de la tarjeta
     * @param fechaVencimiento Fecha de vencimiento de la tarjeta (MM/YY)
     * @param tipoOperacion Tipo de operación (ej: COMPRA)
     * @return Transacción procesada con estado y código de respuesta
     */
    Transaccion procesarPago(String idComercio, BigDecimal monto, String moneda, 
                             String tokenTarjeta, String fechaVencimiento, String tipoOperacion);

    /**
     * Consulta una transacción por su ID.
     *
     * @param transactionId ID de la transacción
     * @return Transacción encontrada
     */
    Transaccion obtenerTransaccionPorId(String transactionId);

    /**
     * Lista transacciones filtradas por comercio y/o estado.
     *
     * @param merchantId ID del comercio (opcional)
     * @param status Estado de la transacción (opcional)
     * @return Lista de transacciones que cumplen los filtros
     */
    List<Transaccion> listarTransacciones(String merchantId, String status);
}
