package com.example.microservices.controller;

import com.example.microservices.dto.DtoMovimiento;
import com.example.microservices.entity.Movimiento;
import com.example.microservices.service.MovimientoService;
import com.example.microservices.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Define que esta clase es un controlador REST
@RequestMapping("/movimientos")  // Ruta base para las solicitudes relacionadas con los movimientos
public class MovimientoController {

    private final MovimientoService movimientoService;  // Inyección del servicio

    // Constructor para inyectar el servicio de Movimiento
    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }    
            
    // Método para registrar múltiples movimientos
    @PostMapping("/registrar")
    public ResponseEntity<List<Movimiento>> registrarMovimientos(
            @RequestBody List<DtoMovimiento> listaDtoMovimientos) {
        
        // Llamada al servicio para registrar los movimientos
        List<Movimiento> movimientos = movimientoService.registrarMovimientos(listaDtoMovimientos);
        
        // Retorna una respuesta con los movimientos registrados
        return ResponseEntity.ok(movimientos);
    }
}
