package com.example.microservices.service;

import com.example.microservices.entity.Cliente;
import com.example.microservices.entity.Cuenta;
import com.example.microservices.dto.*;
import com.example.microservices.repository.ClienteRepository;
import com.example.microservices.repository.CuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    public CuentaService(CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Cuenta> obtenerCuentasPorIdentificacion(String identificacion) {
        Cliente cliente = clienteRepository.findByIdentificacion(identificacion);

        if (cliente == null) { // ✅ Verificamos si el cliente es nulo
            throw new RuntimeException("Cliente no encontrado con identificación: " + identificacion);
        }

        return cuentaRepository.findByCliente(cliente);
    }
}
