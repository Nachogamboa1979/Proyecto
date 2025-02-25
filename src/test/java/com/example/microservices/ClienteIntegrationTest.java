package com.example.microservices;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.microservices.entity.Cliente;
import com.example.microservices.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.UUID;

@Testcontainers
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("postgres_db")
            .withUsername("postgres")
            .withPassword("Emelec1979");

    static {
        System.out.println("Iniciando Testcontainers PostgreSQL...");
        postgreSQLContainer.start();
        System.out.println("Contenedor iniciado con URL: " + postgreSQLContainer.getJdbcUrl());
    }        //.withExposedPorts(0);  // ← Puerto dinámico

    @Autowired
    private ClienteRepository clienteRepository;

    // Método para configurar dinámicamente las propiedades de conexión a la base de datos
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Esperar a que el contenedor esté completamente iniciado
        while (!postgreSQLContainer.isRunning()) {
            try {
                Thread.sleep(1000);  // Espera 1 segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("JDBC URL: " + postgreSQLContainer.getJdbcUrl()); 
        // Configuración de las propiedades para Spring Boot
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);        
    }

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        clienteRepository.deleteAll();
    }

    @Test
    void testGuardarYBuscarCliente() {
        // Crear un nuevo cliente para guardar
        Cliente cliente = new Cliente();
        cliente.setClienteId(UUID.randomUUID().toString()); 
        cliente.setIdentificacion("1702546520");
        cliente.setNombre("Juan Pérez");
        cliente.setDireccion("Las Orquideas");
        clienteRepository.save(cliente);

        // Buscar el cliente por su identificación
        Cliente clienteRecuperado = clienteRepository.findByIdentificacion("1702546520");

        // Verificar que el cliente recuperado no sea nulo y que el nombre sea el esperado
        assertThat(clienteRecuperado).isNotNull();
        assertThat(clienteRecuperado.getNombre()).isEqualTo("Juan Pérez");
    }
}
