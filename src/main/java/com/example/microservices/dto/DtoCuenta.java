package com.example.microservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCuenta {
    private String identificacion;
    private String tipoCuenta;
    private String numeroCuenta;
    private BigDecimal saldoInicial;
    private Boolean estado;
}
