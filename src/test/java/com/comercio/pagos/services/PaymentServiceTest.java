package com.comercio.pagos.services;

import com.comercio.pagos.entities.Comercio;
import com.comercio.pagos.entities.Transaccion;
import com.comercio.pagos.repositories.ComercioRepository;
import com.comercio.pagos.repositories.TransaccionRepository;
import com.comercio.pagos.services.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests básicos para reglas de negocio de pagos
 */
class PaymentServiceTest {

    private PaymentServiceImpl paymentService;

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ComercioRepository comercioRepository;

    private Comercio comercioActivo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(transaccionRepository, comercioRepository);
        // Configurar el patrón de tarjeta usando reflection para tests
        try {
            var field = PaymentServiceImpl.class.getDeclaredField("patronTarjetaRechazo");
            field.setAccessible(true);
            field.set(paymentService, "^4111111111111111$");
        } catch (Exception e) {
            // Ignorar si falla
        }

        // Comercio de prueba
        comercioActivo = new Comercio();
        comercioActivo.setId(1L);
        comercioActivo.setIdComercio("COM001");
        comercioActivo.setNombre("Tienda Test");
        comercioActivo.setMontoMaximoTransaccion(new BigDecimal("10000.00"));
        comercioActivo.setEstado("ACTIVO");
        comercioActivo.setFechaCreacion(System.currentTimeMillis());
    }

    // Test 1: Comercio no existe
    @Test
    void testComercioNoExiste() {
        when(comercioRepository.findByIdComercio("COM999")).thenReturn(Optional.empty());
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = paymentService.procesarPago("COM999", new BigDecimal("1000"), 
            "USD", "tok_123", "12/25", "COMPRA");

        assertNotNull(resultado);
        assertEquals("ERROR", resultado.getEstado());
        assertEquals("E999", resultado.getCodigoRespuesta());
        assertTrue(resultado.getMensajeRespuesta().contains("Comercio no encontrado"));
    }



    // Test 2: Comercio inactivo rechaza transacción
    @Test
    void testComercioInactivoRechaza() {
        comercioActivo.setEstado("INACTIVO");
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = paymentService.procesarPago("COM001", new BigDecimal("1000"), 
            "USD", "tok_123", "12/25", "COMPRA");

        assertNotNull(resultado);
        assertEquals("ERROR", resultado.getEstado());
        assertEquals("E999", resultado.getCodigoRespuesta());
        assertTrue(resultado.getMensajeRespuesta().contains("Comercio inactivo"));
    }



    // Test 3: Monto mayor al límite es rechazado
    @Test
    void testMontoExcedeLimite() {
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaccion resultado = paymentService.procesarPago(
            "COM001", 
            new BigDecimal("15000.00"),  // Excede el límite de 10000
            "USD", 
            "tok_123", 
            "12/25", 
            "COMPRA"
        );

        assertEquals("DECLINED", resultado.getEstado());
        assertEquals("E001", resultado.getCodigoRespuesta());
        assertTrue(resultado.getMensajeRespuesta().contains("límite"));
    }

    // Test 4: Tarjeta simulada es rechazada
    @Test
    void testTarjetaSimuladaRechazada() {
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaccion resultado = paymentService.procesarPago(
            "COM001", 
            new BigDecimal("1000.00"),
            "USD", 
            "4111111111111111",  // Tarjeta simulada por defecto
            "12/25", 
            "COMPRA"
        );

        assertEquals("DECLINED", resultado.getEstado());
        assertEquals("E002", resultado.getCodigoRespuesta());
        assertTrue(resultado.getMensajeRespuesta().contains("Tarjeta no permitida"));
    }

    // Test 5: Transacción válida genera ID único
    @Test
    void testTransaccionGeneraIdUnico() {
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaccion resultado = paymentService.procesarPago(
            "COM001", 
            new BigDecimal("1000.00"),
            "USD", 
            "tok_valid_card", 
            "12/25", 
            "COMPRA"
        );

        assertNotNull(resultado.getIdTransaccion());
        assertTrue(resultado.getIdTransaccion().startsWith("TXN-"));
    }

    // Test 6: Transacción válida tiene estado APPROVED o DECLINED
    @Test
    void testTransaccionTieneEstadoValido() {
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaccion resultado = paymentService.procesarPago(
            "COM001", 
            new BigDecimal("1000.00"),
            "USD", 
            "tok_valid_card", 
            "12/25", 
            "COMPRA"
        );

        // El issuer mock responde aleatoriamente, pero debe ser APPROVED o DECLINED
        assertTrue(resultado.getEstado().equals("APPROVED") || 
                   resultado.getEstado().equals("DECLINED"));
        assertNotNull(resultado.getCodigoRespuesta());
        assertNotNull(resultado.getFechaProcesada());
    }

    // Test 7: Transacción guarda todos los datos
    @Test
    void testTransaccionGuardaTodosLosDatos() {
        when(comercioRepository.findByIdComercio("COM001")).thenReturn(Optional.of(comercioActivo));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaccion resultado = paymentService.procesarPago(
            "COM001", 
            new BigDecimal("1500.50"),
            "USD", 
            "tok_123456", 
            "12/25", 
            "COMPRA"
        );

        assertEquals("COM001", resultado.getIdComercio());
        assertEquals(new BigDecimal("1500.50"), resultado.getMonto());
        assertEquals("USD", resultado.getMoneda());
        assertEquals("tok_123456", resultado.getTokenTarjeta());
        assertEquals("12/25", resultado.getFechaVencimiento());
        assertEquals("COMPRA", resultado.getTipoOperacion());
        assertNotNull(resultado.getFechaCreacion());
    }
}
