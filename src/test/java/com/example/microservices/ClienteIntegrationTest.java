package com.example.microservices;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.microservices.entity.Cliente;
import com.example.microservices.entity.Cuenta;
import com.example.microservices.entity.Movimiento;
import com.example.microservices.repository.ClienteRepository;
import com.example.microservices.repository.CuentaRepository;
import com.example.microservices.repository.MovimientoRepository;
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
import java.math.BigDecimal;
import java.util.Optional;

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

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

        
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
        cuentaRepository.deleteAll(); 
        movimientoRepository.deleteAll();       
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

    @Test
    void testGuardarClienteCuentaMovimiento() {
        // Crear y guardar un cliente
        Cliente cliente = new Cliente();
        cliente.setClienteId(UUID.randomUUID().toString());
        cliente.setIdentificacion("1702546520");
        cliente.setNombre("Juan Pérez");
        cliente.setDireccion("Las Orquideas");
        clienteRepository.save(cliente);

        // Verificar que el cliente se haya guardado
        Cliente clienteRecuperado = clienteRepository.findByIdentificacion("1702546520");
        assertThat(clienteRecuperado).isNotNull();
        assertThat(clienteRecuperado.getNombre()).isEqualTo("Juan Pérez");

        // Crear y guardar una cuenta para el cliente
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("123456789");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("500.00"));
        cuenta.setEstado(true);
        cuenta.setCliente(clienteRecuperado);
        cuentaRepository.save(cuenta);

        // Verificar que la cuenta se haya guardado        
        Cuenta cuentaRecuperada = cuentaRepository.findByNumeroCuenta("123456789")
        .orElseThrow(() -> new AssertionError("La cuenta no fue encontrada"));
        assertThat(cuentaRecuperada.getSaldoInicial()).isEqualByComparingTo("500.00");

        // Crear y guardar un movimiento en la cuenta
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento("Deposito");
        movimiento.setValor(new BigDecimal("200.00"));
        movimiento.setSaldo(cuentaRecuperada.getSaldoInicial().add(movimiento.getValor()));
        movimiento.setCuenta(cuentaRecuperada);
        movimientoRepository.save(movimiento);

        // Verificar que el movimiento se haya guardado
        Movimiento movimientoRecuperado = movimientoRepository.findAll().get(0);
        assertThat(movimientoRecuperado).isNotNull();
        assertThat(movimientoRecuperado.getTipoMovimiento()).isEqualTo("Deposito");
        assertThat(movimientoRecuperado.getValor()).isEqualByComparingTo("200.00");
    }
}
