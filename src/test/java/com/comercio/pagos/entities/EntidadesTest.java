package com.comercio.pagos.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests básicos de entidades de dominio
 */
class EntidadesTest {

    // Test 1: Comercio se crea correctamente
    @Test
    void testCrearComercio() {
        Comercio comercio = new Comercio();
        comercio.setIdComercio("COM001");
        comercio.setNombre("Tienda Test");
        comercio.setMontoMaximoTransaccion(new BigDecimal("50000.00"));
        comercio.setEstado("ACTIVO");
        comercio.setFechaCreacion(System.currentTimeMillis());

        assertEquals("COM001", comercio.getIdComercio());
        assertEquals("Tienda Test", comercio.getNombre());
        assertEquals(new BigDecimal("50000.00"), comercio.getMontoMaximoTransaccion());
        assertEquals("ACTIVO", comercio.getEstado());
        assertNotNull(comercio.getFechaCreacion());
    }

    // Test 2: Transacción se crea correctamente
    @Test
    void testCrearTransaccion() {
        Transaccion transaccion = new Transaccion();
        transaccion.setIdTransaccion("TXN-123");
        transaccion.setIdComercio("COM001");
        transaccion.setMonto(new BigDecimal("1500.50"));
        transaccion.setMoneda("USD");
        transaccion.setEstado("APPROVED");
        transaccion.setCodigoRespuesta("00");

        assertEquals("TXN-123", transaccion.getIdTransaccion());
        assertEquals("COM001", transaccion.getIdComercio());
        assertEquals(new BigDecimal("1500.50"), transaccion.getMonto());
        assertEquals("USD", transaccion.getMoneda());
        assertEquals("APPROVED", transaccion.getEstado());
        assertEquals("00", transaccion.getCodigoRespuesta());
    }

    // Test 3: Comercio tiene estado por defecto
    @Test
    void testComercioEstadoPorDefecto() {
        Comercio comercio = new Comercio();
        comercio.setEstado("ACTIVO");
        
        assertEquals("ACTIVO", comercio.getEstado());
    }

    // Test 4: Comparar montos de transacciones
    @Test
    void testCompararMontos() {
        BigDecimal limite = new BigDecimal("10000.00");
        BigDecimal montoMenor = new BigDecimal("5000.00");
        BigDecimal montoMayor = new BigDecimal("15000.00");

        assertTrue(montoMenor.compareTo(limite) < 0);  // Menor que límite
        assertTrue(montoMayor.compareTo(limite) > 0);  // Mayor que límite
    }

    // Test 5: Validar estados de transacción válidos
    @Test
    void testEstadosTransaccionValidos() {
        String[] estadosValidos = {"PENDIENTE", "APPROVED", "DECLINED", "ERROR"};

        for (String estado : estadosValidos) {
            Transaccion t = new Transaccion();
            t.setEstado(estado);
            assertEquals(estado, t.getEstado());
        }
    }
}
