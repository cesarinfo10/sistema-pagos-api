package com.comercio.pagos.services;

import com.comercio.pagos.entities.Transaccion;
import java.util.List;

/**
 * Servicio para operaciones CRUD de transacciones.
 * Para procesamiento de pagos, usar PaymentService.
 */
public interface TransaccionService {

    /**
     * Obtiene una transacción por su ID.
     *
     * @param idTransaccion ID de la transacción
     * @return Transacción encontrada
     */
    Transaccion obtenerTransaccion(String idTransaccion);

    /**
     * Lista todas las transacciones.
     *
     * @return Lista de transacciones
     */
    List<Transaccion> listarTransacciones();

    /**
     * Lista transacciones filtradas por comercio y/o estado.
     *
     * @param idComercio ID del comercio (opcional)
     * @param estado Estado de la transacción (opcional)
     * @return Lista de transacciones filtradas
     */
    List<Transaccion> listarTransaccionesFiltradas(String idComercio, String estado);

    /**
     * Lista transacciones de un comercio específico.
     *
     * @param idComercio ID del comercio
     * @return Lista de transacciones del comercio
     */
    List<Transaccion> listarTransaccionesPorComercio(String idComercio);

    /**
     * Lista transacciones por estado.
     *
     * @param estado Estado de las transacciones
     * @return Lista de transacciones con el estado especificado
     */
    List<Transaccion> listarTransaccionesPorEstado(String estado);
}

