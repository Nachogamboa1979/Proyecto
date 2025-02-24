package com.example.microservices.controller;

import com.example.microservices.entity.Cliente;
import com.example.microservices.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository clienteRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    public ClienteController(ClienteRepository clienteRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.clienteRepository = clienteRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Obtener todos los clientes
    @GetMapping
    public List<Cliente> getAllClientes() {
        try {
            return clienteRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener los clientes", e);
        }
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
    }

    // Crear un nuevo cliente    
    /*@PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente savedCliente = clienteRepository.save(cliente);

            // Publicar evento en Kafka
            kafkaTemplate.send("clientes-topic", savedCliente);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el cliente", e);
        }
    }*/
    
    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente savedCliente = clienteRepository.save(cliente);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String clienteJson = objectMapper.writeValueAsString(savedCliente);
                kafkaTemplate.send("clientes-topic", clienteJson);
            } catch (Exception kafkaException) {
                logger.error("Error al enviar mensaje a Kafka", kafkaException);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
        } catch (Exception e) {
            logger.error("Error al guardar el cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    
    @PostMapping("/bulk")
    public ResponseEntity<List<Cliente>> createClientes(@RequestBody List<Cliente> clientes) {
        try {
            List<Cliente> nuevosClientes = clientes.stream()
                    .filter(cliente -> !clienteRepository.existsByIdentificacion(cliente.getIdentificacion()))
                    .toList();

            List<Cliente> savedClientes = clienteRepository.saveAll(nuevosClientes);

            // Publicar cada cliente en Kafka
            savedClientes.forEach(cliente -> kafkaTemplate.send("clientes-topic", cliente));

            return ResponseEntity.status(HttpStatus.CREATED).body(savedClientes);
        } catch (Exception e) {
            logger.error("Error al procesar los clientes", e); // Log del error completo
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar los clientes", e);
        }
    }


    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Actualización de los campos no nulos
                    if (clienteDetails.getNombre() != null) cliente.setNombre(clienteDetails.getNombre());
                    if (clienteDetails.getGenero() != null) cliente.setGenero(clienteDetails.getGenero());
                    if (clienteDetails.getEdad() != null) cliente.setEdad(clienteDetails.getEdad());
                    if (clienteDetails.getTelefono() != null) cliente.setTelefono(clienteDetails.getTelefono());
                    if (clienteDetails.getDireccion() != null) cliente.setDireccion(clienteDetails.getDireccion());

                    Cliente updatedCliente = clienteRepository.save(cliente);
                    return ResponseEntity.ok(updatedCliente);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
    }

}
