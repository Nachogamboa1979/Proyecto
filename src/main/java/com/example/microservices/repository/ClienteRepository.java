package com.example.microservices.repository;

import com.example.microservices.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}
