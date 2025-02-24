package com.example.microservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;  // Cambiar a LocalDateTime

@Data
@Entity
@Table(name = "movimiento")
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usar LocalDateTime para representar fecha y hora
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String tipoMovimiento;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "idcuenta", nullable = false)
    private Cuenta cuenta;

    // Asignar la fecha y hora actuales antes de persistir
    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now(); // Asigna la fecha y hora actual antes de persistir
    }
}
