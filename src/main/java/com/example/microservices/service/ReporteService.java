package com.example.microservices.service;

import com.example.microservices.dto.DtoEstadoCuenta;
import com.example.microservices.repository.CuentaRepository;
import com.example.microservices.repository.MovimientoRepository;
import com.example.microservices.entity.Cuenta;
import com.example.microservices.entity.Movimiento;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public DtoEstadoCuenta generarReporteEstadoCuenta(String identificacion, String fechaDesde, String fechaHasta) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date fechaInicio = sdf.parse(fechaDesde);
        Date fechaFin = sdf.parse(fechaHasta);

        // Convierte Date a LocalDateTime
        LocalDateTime localFechaInicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localFechaFin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


        List<Cuenta> cuentas = cuentaRepository.findByCliente_Identificacion(identificacion);  // Método que busca las cuentas asociadas al cliente por su identificación
        if (cuentas.isEmpty()) {
            throw new RuntimeException("No se encontraron cuentas para el cliente con identificación: " + identificacion);
        }

        List<Map<String, Object>> cuentasInfo = new ArrayList<>();
        List<Map<String, Object>> movimientosInfo = new ArrayList<>();

        // Obtener información de las cuentas
        for (Cuenta cuenta : cuentas) {
            Map<String, Object> cuentaMap = new HashMap<>();
            cuentaMap.put("nombreCliente", cuenta.getCliente().getNombre());  // Asumiendo que la relación cuenta-cliente existe
            cuentaMap.put("tipoCuenta", cuenta.getTipoCuenta());
            cuentaMap.put("numeroCuenta", cuenta.getNumeroCuenta());
            cuentaMap.put("saldo", cuenta.getSaldoInicial());
            cuentasInfo.add(cuentaMap);

            // Obtener los movimientos de la cuenta dentro del rango de fechas
            List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(cuenta, localFechaInicio, localFechaFin);
            for (Movimiento movimiento : movimientos) {
                Map<String, Object> movimientoMap = new HashMap<>();
                movimientoMap.put("numeroCuenta", cuenta.getNumeroCuenta());
                movimientoMap.put("fecha", movimiento.getFecha());
                movimientoMap.put("tipoMovimiento", movimiento.getTipoMovimiento());
                // Usamos equals() para comparar el tipo de movimiento
                movimientoMap.put("saldoinicial", 
                        movimiento.getTipoMovimiento().equals("Deposito") 
                            ? movimiento.getSaldo().subtract(movimiento.getValor()) 
                            : movimiento.getValor().subtract(movimiento.getSaldo())
                    );
                movimientoMap.put("monto", movimiento.getValor());
                movimientoMap.put("saldo", movimiento.getSaldo());
                movimientosInfo.add(movimientoMap);
            }
        }

        DtoEstadoCuenta dtoEstadoCuenta = new DtoEstadoCuenta();
        dtoEstadoCuenta.setIdentificacion(identificacion);
        dtoEstadoCuenta.setFechaDesde(fechaInicio);
        dtoEstadoCuenta.setFechaHasta(fechaFin);
        dtoEstadoCuenta.setCuentas(cuentasInfo);
        dtoEstadoCuenta.setMovimientos(movimientosInfo);

        return dtoEstadoCuenta;
    }
}
