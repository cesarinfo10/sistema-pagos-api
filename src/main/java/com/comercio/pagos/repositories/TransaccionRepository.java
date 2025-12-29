package com.comercio.pagos.repositories;

import com.comercio.pagos.entities.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Transacción.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    /**
     * Busca una transacción por su ID único.
     */
    Optional<Transaccion> findByIdTransaccion(String idTransaccion);

    /**
     * Lista transacciones por ID de comercio.
     */
    List<Transaccion> findByIdComercio(String idComercio);

    /**
     * Lista transacciones por estado.
     */
    List<Transaccion> findByEstado(String estado);

    /**
     * Lista transacciones por comercio y estado.
     */
    List<Transaccion> findByIdComercioAndEstado(String idComercio, String estado);
}
