package com.example.microservices.repository;

import com.example.microservices.entity.Cliente;
import com.example.microservices.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    List<Cuenta> findByCliente_Identificacion(String identificacion); // Consulta de cuentas por Identificación
    List<Cuenta> findByCliente(Cliente cliente); // ✅ Consulta por objeto Cliente
}
