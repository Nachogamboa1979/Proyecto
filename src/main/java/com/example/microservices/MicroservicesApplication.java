package com.example.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.microservices.entity") // 🔹 Asegura que detecta las entidades
public class MicroservicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicesApplication.class, args);
    }
}
