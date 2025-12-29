package com.comercio.pagos.controllers;

import com.comercio.pagos.entities.Comercio;
import com.comercio.pagos.services.ComercioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar comercios.
 */
@RestController
@RequestMapping("/comercios")
public class ComercioController {

    private final ComercioService comercioService;

    public ComercioController(ComercioService comercioService) {
        this.comercioService = comercioService;
    }

    // POST /comercios - Registrar nuevo comercio
    @PostMapping
    public ResponseEntity<?> registrarComercio(@RequestBody Comercio comercio) {
        try {
            Comercio comercioGuardado = comercioService.registrarComercio(comercio);
            return ResponseEntity.status(HttpStatus.CREATED).body(comercioGuardado);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error registrando comercio");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // GET /comercios/{idComercio} - Obtener un comercio
    @GetMapping("/{idComercio}")
    public ResponseEntity<?> obtenerComercio(@PathVariable String idComercio) {
        try {
            Comercio comercio = comercioService.obtenerComercio(idComercio);
            return ResponseEntity.ok(comercio);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Comercio no encontrado");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // GET /comercios - Listar todos los comercios
    @GetMapping
    public ResponseEntity<List<Comercio>> listarComercios() {
        List<Comercio> comercios = comercioService.listarComercios();
        return ResponseEntity.ok(comercios);
    }

    // PUT /comercios/{idComercio}/estado - Actualizar estado de un comercio
    @PutMapping("/{idComercio}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable String idComercio,
            @RequestBody Map<String, String> body) {
        
        try {
            String nuevoEstado = body.get("estado");
            Comercio comercio = comercioService.actualizarEstado(idComercio, nuevoEstado);
            return ResponseEntity.ok(comercio);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error actualizando estado");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
