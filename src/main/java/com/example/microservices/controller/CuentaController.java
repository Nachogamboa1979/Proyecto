package com.example.microservices.controller;

import com.example.microservices.entity.Cliente;  // Asegúrate de importar el modelo Cliente
import com.example.microservices.entity.Cuenta;
import com.example.microservices.dto.DtoCuenta;
import com.example.microservices.util.*;
import com.example.microservices.exception.EntityNotFoundException;
import com.example.microservices.exception.InvalidDataException;
import com.example.microservices.repository.ClienteRepository;  // Asegúrate de tener el repositorio del Cliente
import com.example.microservices.repository.CuentaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cuentas")  // Define la ruta base para las solicitudes de 'Cuenta'
public class CuentaController {
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;  // Inyectar el repositorio Cliente

    // Constructor para inyectar los repositorios de Cuenta y Cliente
    public CuentaController(CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    // Método para obtener todas las cuentas
    @GetMapping
    public List<Cuenta> getAllCuentas() {
        return cuentaRepository.findAll();
    }

    // Método para crear una nueva cuenta
    @PostMapping
    public ResponseEntity<Cuenta> createCuenta(@RequestBody DtoCuenta dtoCuenta) {
        if (dtoCuenta == null || dtoCuenta.getTipoCuenta() == null || dtoCuenta.getSaldoInicial() == null || dtoCuenta.getNumeroCuenta() == null) {
            throw new InvalidDataException("Datos incompletos para crear la cuenta.");
        }

        Cliente cliente = clienteRepository.findByIdentificacion(dtoCuenta.getIdentificacion());
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente con identificación " + dtoCuenta.getIdentificacion() + " no encontrado.");
        }

        // Crear y mapear la entidad
        Cuenta cuenta = MapearUtil.mapDtoToEntity(dtoCuenta, new Cuenta());
        cuenta.setCliente(cliente);

        return ResponseEntity.ok(cuentaRepository.save(cuenta));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Cuenta>> bulkCreateCuentas(@RequestBody List<DtoCuenta> dtoCuentas) {
        if (dtoCuentas == null || dtoCuentas.isEmpty()) {
            throw new InvalidDataException("La lista de cuentas no puede estar vacía.");
        }

        List<Cuenta> cuentas = dtoCuentas.stream().map(dtoCuenta -> {
            if (dtoCuenta.getTipoCuenta() == null || dtoCuenta.getSaldoInicial() == null || dtoCuenta.getNumeroCuenta() == null) {
                throw new InvalidDataException("Datos incompletos para crear la cuenta con número: " + dtoCuenta.getNumeroCuenta());
            }

            Cliente cliente = clienteRepository.findByIdentificacion(dtoCuenta.getIdentificacion());
            if (cliente == null) {
                throw new EntityNotFoundException("Cliente con identificación " + dtoCuenta.getIdentificacion() + " no encontrado.");
            }

            Cuenta cuenta = MapearUtil.mapDtoToEntity(dtoCuenta, new Cuenta());
            cuenta.setCliente(cliente);
            return cuenta;
        }).toList();

        return ResponseEntity.ok(cuentaRepository.saveAll(cuentas));
    }


    // Método para actualizar una cuenta existente
    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> updateCuenta(@PathVariable Long id, @RequestBody Cuenta cuenta) {
        if (!cuentaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cuenta con id " + id + " no encontrada.");
        }
        cuenta.setId(id);
        return ResponseEntity.ok(cuentaRepository.save(cuenta));
    }

    // Método para eliminar una cuenta por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuenta(@PathVariable Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cuenta con id " + id + " no encontrada.");
        }
        cuentaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
