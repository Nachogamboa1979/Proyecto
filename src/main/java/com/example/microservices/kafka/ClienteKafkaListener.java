package com.example.microservices.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClienteKafkaListener {

    @KafkaListener(topics = "cliente-topic", groupId = "movimiento-group")
    public void listen(String message) {
        // Lógica para crear una cuenta asociada al cliente recibido
        System.out.println("Mensaje recibido: " + message);
        // Crear una cuenta automáticamente o procesar el movimiento
    }
}
