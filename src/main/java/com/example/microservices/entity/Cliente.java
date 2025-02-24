package com.example.microservices.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@PrimaryKeyJoinColumn(name = "persona_id") // 🔹 Indica que la clave primaria de Cliente es la misma que en Persona
public class Cliente extends Persona {
    @Column(unique = true, nullable = false) // 🔹 Evita valores nulos en clienteId
    private String clienteId;
    
    private String password;
    private Boolean estado;
}
