package com.comercio.pagos.services.impl;

import com.comercio.pagos.entities.Transaccion;
import com.comercio.pagos.repositories.TransaccionRepository;
import com.comercio.pagos.services.TransaccionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de consulta de transacciones.
 * Proporciona operaciones de lectura sobre transacciones existentes.
 */
@Service
@Transactional(readOnly = true)
public class TransaccionServiceImpl implements TransaccionService {

    private static final Logger log = LoggerFactory.getLogger(TransaccionServiceImpl.class);

    private final TransaccionRepository transaccionRepository;

    public TransaccionServiceImpl(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @Override
    public Transaccion obtenerTransaccion(String idTransaccion) {
        log.debug("Consultando transacción: {}", idTransaccion);
        return transaccionRepository.findByIdTransaccion(idTransaccion)
                .orElseThrow(() -> {
                    log.error("Transacción no encontrada: {}", idTransaccion);
                    return new RuntimeException("Transacción no encontrada: " + idTransaccion);
                });
    }

    @Override
    public List<Transaccion> listarTransacciones() {
        log.debug("Listando todas las transacciones");
        return transaccionRepository.findAll();
    }

    @Override
    public List<Transaccion> listarTransaccionesFiltradas(String idComercio, String estado) {
        log.debug("Listando transacciones - Comercio: {}, Estado: {}", idComercio, estado);
        
        if (idComercio != null && estado != null) {
            log.debug("Filtrando por comercio y estado");
            return transaccionRepository.findByIdComercioAndEstado(idComercio, estado);
        } else if (idComercio != null) {
            log.debug("Filtrando solo por comercio");
            return transaccionRepository.findByIdComercio(idComercio);
        } else if (estado != null) {
            log.debug("Filtrando solo por estado");
            return transaccionRepository.findByEstado(estado);
        } else {
            log.debug("Sin filtros, listando todas");
            return transaccionRepository.findAll();
        }
    }

    @Override
    public List<Transaccion> listarTransaccionesPorComercio(String idComercio) {
        log.debug("Listando transacciones del comercio: {}", idComercio);
        return transaccionRepository.findByIdComercio(idComercio);
    }

    @Override
    public List<Transaccion> listarTransaccionesPorEstado(String estado) {
        log.debug("Listando transacciones con estado: {}", estado);
        return transaccionRepository.findByEstado(estado);
    }
}
