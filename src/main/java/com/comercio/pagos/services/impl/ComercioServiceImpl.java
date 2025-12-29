package com.comercio.pagos.services.impl;

import com.comercio.pagos.entities.Comercio;
import com.comercio.pagos.repositories.ComercioRepository;
import com.comercio.pagos.services.ComercioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de comercios.
 */
@Service
@Transactional
public class ComercioServiceImpl implements ComercioService {

    private static final Logger log = LoggerFactory.getLogger(ComercioServiceImpl.class);

    private final ComercioRepository comercioRepository;

    public ComercioServiceImpl(ComercioRepository comercioRepository) {
        this.comercioRepository = comercioRepository;
    }

    @Override
    public Comercio registrarComercio(Comercio comercio) {
        log.info("Registrando nuevo comercio: {}", comercio.getIdComercio());
        
        // Validar que no exista
        if (comercioRepository.findByIdComercio(comercio.getIdComercio()).isPresent()) {
            log.error("El comercio {} ya existe", comercio.getIdComercio());
            throw new RuntimeException("El comercio ya existe: " + comercio.getIdComercio());
        }

        // Establecer valores por defecto
        if (comercio.getEstado() == null) {
            comercio.setEstado("ACTIVO");
        }
        if (comercio.getFechaCreacion() == null) {
            comercio.setFechaCreacion(System.currentTimeMillis());
        }

        Comercio comercioGuardado = comercioRepository.save(comercio);
        log.info("Comercio registrado exitosamente: {}", comercioGuardado.getIdComercio());
        
        return comercioGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Comercio obtenerComercio(String idComercio) {
        log.debug("Consultando comercio: {}", idComercio);
        return comercioRepository.findByIdComercio(idComercio)
                .orElseThrow(() -> {
                    log.error("Comercio no encontrado: {}", idComercio);
                    return new RuntimeException("Comercio no encontrado: " + idComercio);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comercio> listarComercios() {
        log.debug("Listando todos los comercios");
        return comercioRepository.findAll();
    }

    @Override
    public Comercio actualizarEstado(String idComercio, String nuevoEstado) {
        log.info("Actualizando estado del comercio {} a {}", idComercio, nuevoEstado);
        
        Comercio comercio = obtenerComercio(idComercio);
        comercio.setEstado(nuevoEstado);
        
        Comercio comercioActualizado = comercioRepository.save(comercio);
        log.info("Estado actualizado exitosamente para comercio: {}", idComercio);
        
        return comercioActualizado;
    }
}
