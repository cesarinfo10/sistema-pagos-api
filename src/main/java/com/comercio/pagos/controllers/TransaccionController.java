package com.comercio.pagos.controllers;

import com.comercio.pagos.entities.Transaccion;
import com.comercio.pagos.services.TransaccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para consultar transacciones.
 * Para PROCESAR pagos, usar PaymentController.
 */
@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // GET /transacciones/{idTransaccion} - Consultar una transacción
    @GetMapping("/{idTransaccion}")
    public ResponseEntity<?> obtenerTransaccion(@PathVariable String idTransaccion) {
        try {
            Transaccion transaccion = transaccionService.obtenerTransaccion(idTransaccion);
            return ResponseEntity.ok(transaccion);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Transacción no encontrada");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // GET /transacciones - Listar todas las transacciones
    @GetMapping
    public ResponseEntity<List<Transaccion>> listarTransacciones() {
        List<Transaccion> transacciones = transaccionService.listarTransacciones();
        return ResponseEntity.ok(transacciones);
    }

    // GET /transacciones/comercio/{idComercio} - Transacciones de un comercio
    @GetMapping("/comercio/{idComercio}")
    public ResponseEntity<List<Transaccion>> listarPorComercio(@PathVariable String idComercio) {
        List<Transaccion> transacciones = transaccionService.listarTransaccionesPorComercio(idComercio);
        return ResponseEntity.ok(transacciones);
    }

    // GET /transacciones/estado/{estado} - Transacciones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Transaccion>> listarPorEstado(@PathVariable String estado) {
        List<Transaccion> transacciones = transaccionService.listarTransaccionesPorEstado(estado);
        return ResponseEntity.ok(transacciones);
    }

    // GET /transacciones/filtrar?comercio=...&estado=... - Filtrado combinado
    @GetMapping("/filtrar")
    public ResponseEntity<List<Transaccion>> filtrarTransacciones(
            @RequestParam(required = false) String comercio,
            @RequestParam(required = false) String estado) {
        
        List<Transaccion> transacciones = transaccionService.listarTransaccionesFiltradas(comercio, estado);
        return ResponseEntity.ok(transacciones);
    }
}
