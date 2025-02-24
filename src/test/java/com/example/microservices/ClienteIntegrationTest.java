package com.example.microservices;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.microservices.entity.Cliente;
import com.example.microservices.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = 
        new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        // Configurar la base de datos antes de cada prueba
        postgreSQLContainer.start();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @Test
    void testGuardarYBuscarCliente() {
        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setIdentificacion("1702546520");
        cliente.setNombre("Juan Pérez");        
        cliente.setDireccion("Las Orquideas");
        clienteRepository.save(cliente);

        // Buscar cliente por email
        Cliente clienteRecuperado = clienteRepository.findByIdentificacion("1702546520");

        // Verificar que el cliente se guardó correctamente
        assertThat(clienteRecuperado);
        assertThat(clienteRecuperado.getNombre()).isEqualTo("Juan Pérez");
    }
}
