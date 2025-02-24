package com.example.microservices.service;

import com.example.microservices.entity.Cuenta;
import com.example.microservices.entity.Movimiento;
import com.example.microservices.dto.DtoMovimiento;
import com.example.microservices.repository.CuentaRepository;
import com.example.microservices.repository.MovimientoRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoService.class);

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MovimientoService(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public List<Movimiento> registrarMovimientos(List<DtoMovimiento> dtoMovimientos) {
        // Procesar todos los movimientos
        return dtoMovimientos.stream()
                .map(this::registrarMovimiento)  // Usamos el método que ya tienes para registrar un solo movimiento
                .collect(Collectors.toList());
    }


    @Transactional
    public Movimiento registrarMovimiento(DtoMovimiento dtoMovimiento) {
        var tipo = dtoMovimiento.getTipoMovimiento();
        var monto = dtoMovimiento.getValor();
        var numeroCuenta = dtoMovimiento.getNumeroCuenta();
        logger.info("Iniciando registro de movimiento: Cuenta={} Tipo={} Monto={}", numeroCuenta, tipo, monto);

        // Buscar la cuenta asociada al número proporcionado
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> {
                    logger.error("Cuenta con número {} no encontrada", numeroCuenta);
                    return new RuntimeException("Cuenta no encontrada");
                });

        // Validar si hay saldo suficiente para retiros
        if (tipo.equalsIgnoreCase("retiro") && cuenta.getSaldoInicial().compareTo(monto) < 0) {
            logger.error("Saldo insuficiente en la cuenta {} para retirar {}", numeroCuenta, monto);
            throw new RuntimeException("Saldo No Disponible");  // Cambio el mensaje a "Saldo No Disponible"
        }
        
        // Actualizar saldo de la cuenta según el tipo de movimiento
        if (tipo.equalsIgnoreCase("deposito")) {
            cuenta.setSaldoInicial(cuenta.getSaldoInicial().add(monto));
            logger.info("Depósito realizado. Nuevo saldo: {}", cuenta.getSaldoInicial());
        } else if (tipo.equalsIgnoreCase("retiro")) {
            cuenta.setSaldoInicial(cuenta.getSaldoInicial().subtract(monto));
            logger.info("Retiro realizado. Nuevo saldo: {}", cuenta.getSaldoInicial());
        } else {
            logger.error("Tipo de movimiento inválido: {}", tipo);
            throw new RuntimeException("Tipo de movimiento no válido");
        }

        // Crear y guardar el movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setValor(monto);
        movimiento.setSaldo(cuenta.getSaldoInicial()); // Registrar saldo actualizado en el movimiento

        // Guardar la cuenta y el movimiento en la base de datos
        cuentaRepository.save(cuenta);
        movimientoRepository.save(movimiento);
        logger.info("Movimiento registrado exitosamente para la cuenta {}", numeroCuenta);

        // Publicar evento en Kafka
        kafkaTemplate.send("movimientos-topic", movimiento);
        logger.info("Evento enviado a Kafka: {}", movimiento);

        return movimiento;
    }

}
