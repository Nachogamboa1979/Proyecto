package com.example.microservices.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DtoEstadoCuenta {
    private String identificacion; // Identificación del cliente
    private Date fechaDesde;       // Fecha de inicio del rango
    private Date fechaHasta;       // Fecha de fin del rango
    private List<Map<String, Object>> cuentas;  // Cuentas con saldo
    private List<Map<String, Object>> movimientos; // Movimientos de las cuentas

    // Getters y Setters
    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public List<Map<String, Object>> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Map<String, Object>> cuentas) {
        this.cuentas = cuentas;
    }

    public List<Map<String, Object>> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Map<String, Object>> movimientos) {
        this.movimientos = movimientos;
    }
}
