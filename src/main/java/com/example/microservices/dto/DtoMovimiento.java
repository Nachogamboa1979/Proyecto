package com.example.microservices.dto;

import java.math.BigDecimal;

public class DtoMovimiento {

    private String numeroCuenta; // Número de cuenta
    private String tipoMovimiento; // Tipo de movimiento (ej. Depósito, Retiro)
    private BigDecimal valor; // Monto del movimiento

    // Getter para numeroCuenta
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    // Setter para numeroCuenta
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    // Getter para tipoMovimiento
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    // Setter para tipoMovimiento
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    // Getter para valor
    public BigDecimal getValor() {
        return valor;
    }

    // Setter para valor
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    // Método toString (opcional, para depuración)
    @Override
    public String toString() {
        return "DtoMovimiento{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", valor=" + valor +
                '}';
    }
}
