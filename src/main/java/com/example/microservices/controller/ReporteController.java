package com.example.microservices.controller;

import com.example.microservices.dto.DtoEstadoCuenta;
import com.example.microservices.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // Método para obtener el Estado de Cuenta de un cliente considerando su identificación , fecha desde y fecha hasta
    @GetMapping
    public ResponseEntity<?> generarReporteEstadoCuenta(@RequestParam String fechaDesde, @RequestParam String fechaHasta, @RequestParam String identificacion) {
        // Convertir las fechas de String a Date
        try {
            DtoEstadoCuenta reporte = reporteService.generarReporteEstadoCuenta(identificacion, fechaDesde, fechaHasta);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al generar el reporte: " + e.getMessage());
        }
    }
}
