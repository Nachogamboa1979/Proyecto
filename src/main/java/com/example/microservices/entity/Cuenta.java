package com.example.microservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "cuenta")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único de la cuenta
    
    @Column(unique = true, nullable = false)
    private String numeroCuenta; // Número único de la cuenta
    
    @Column(nullable = false)
    private String tipoCuenta; // Tipo de cuenta (ej. Ahorros, Corriente)
    
    @Column(nullable = false)
    private BigDecimal saldoInicial; // Saldo inicial de la cuenta
    
    @Column(nullable = false)
    private Boolean estado; // Estado de la cuenta (Activa/Inactiva)
    
    @ManyToOne
    @JoinColumn(name = "idcliente", nullable = false)
    private Cliente cliente; // Cliente al que pertenece la cuenta
        
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Movimiento> movimientos; // Lista de movimientos asociados a la cuenta
}