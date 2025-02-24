package com.example.microservices.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity // 🔹 Agregamos @Entity para que Hibernate cree la tabla "persona"
@Inheritance(strategy = InheritanceType.JOINED) // 🔹 Indica que se usará herencia JOINED
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String genero;
    private Integer edad;
    private String identificacion;
    private String direccion;
    private String telefono;
}
