package com.example.microservices;

import com.example.microservices.entity.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void testClienteEntity() {
        // Crear una nueva instancia de Cliente
        Cliente cliente = new Cliente();
        
        // Asignar valores usando setters
        cliente.setId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setIdentificacion("123456789");

        // Verificar que los valores fueron asignados correctamente usando los getters
        assertEquals(1L, cliente.getId(), "El ID no es correcto");
        assertEquals("Juan Pérez", cliente.getNombre(), "El nombre no es correcto");
        assertEquals("123456789", cliente.getIdentificacion(), "La identificación no es correcta");
    }
}
