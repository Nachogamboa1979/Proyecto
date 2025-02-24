package com.example.microservices.repository;

import com.example.microservices.entity.Cuenta;
import com.example.microservices.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaAndFechaBetween(Cuenta cuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);  // Método para obtener movimientos por cuenta y rango de fechas
}