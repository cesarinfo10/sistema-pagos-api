package com.comercio.pagos.repositories;

import com.comercio.pagos.entities.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Comercio.
 */
@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {

    /**
     * Busca un comercio por su ID único.
     */
    Optional<Comercio> findByIdComercio(String idComercio);
}
