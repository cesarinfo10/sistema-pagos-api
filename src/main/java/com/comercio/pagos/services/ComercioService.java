package com.comercio.pagos.services;

import com.comercio.pagos.entities.Comercio;
import java.util.List;

/**
 * Servicio para gestionar comercios.
 */
public interface ComercioService {

    /**
     * Registra un nuevo comercio.
     *
     * @param comercio Datos del comercio a registrar
     * @return Comercio registrado
     */
    Comercio registrarComercio(Comercio comercio);

    /**
     * Obtiene un comercio por su ID.
     *
     * @param idComercio ID del comercio
     * @return Comercio encontrado
     */
    Comercio obtenerComercio(String idComercio);

    /**
     * Lista todos los comercios.
     *
     * @return Lista de comercios
     */
    List<Comercio> listarComercios();

    /**
     * Actualiza el estado de un comercio.
     *
     * @param idComercio ID del comercio
     * @param nuevoEstado Nuevo estado (ACTIVO/INACTIVO)
     * @return Comercio actualizado
     */
    Comercio actualizarEstado(String idComercio, String nuevoEstado);
}

