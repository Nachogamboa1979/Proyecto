package com.example.microservices.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Autowired
    private Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        // Obtener la URL de la base de datos principal desde las propiedades de configuración
        String mainDbUrl = env.getProperty("spring.datasource.url");
        String targetDb = "microservices_db"; // Nombre de la base de datos que se desea verificar/crear

        // Validar que la URL de la base de datos no sea nula
        if (mainDbUrl == null) {
            throw new RuntimeException("❌ La URL de la base de datos no está configurada.");
        }

        try (Connection conn = DriverManager.getConnection(mainDbUrl, dbUser, dbPassword);
            Statement stmt = conn.createStatement()) {

                // Imprimir la URL de la base de datos a la que se está conectando
                System.out.println("🔍 Conectando a la base de datos en: " + mainDbUrl);

                // Ejecutar la consulta para verificar si la base de datos existe
                var resultSet = stmt.executeQuery(
                    "SELECT COUNT(*) FROM pg_database WHERE datname = '" + targetDb + "'"
                );

                // Imprimir el resultSet antes de leerlo
                System.out.println("🔎 Resultado de la consulta: " + resultSet);

                // Mover el cursor al primer resultado
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    System.out.println("📊 Cantidad de bases de datos encontradas con nombre '" + targetDb + "': " + count);

                    if (count == 0) {
                        // Si no existe, crear la base de datos
                        stmt.executeUpdate("CREATE DATABASE " + targetDb);
                        System.out.println("✅ Base de datos '" + targetDb + "' creada correctamente.");
                    } else {
                        System.out.println("⚡ La base de datos '" + targetDb + "' ya existe.");
                    }
                } else {
                    System.err.println("⚠️ No se pudo verificar la existencia de la base de datos '" + targetDb + "'.");
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al verificar/crear la base de datos '" + targetDb + "': " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error en la inicialización de la base de datos", e);
            }

            // 🔄 Reiniciar la conexión para utilizar la nueva base de datos
            resetDataSource(targetDb);

                }

    private DataSource resetDataSource(String targetDb) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(env.getProperty("spring.datasource.secondary.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.secondary.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.secondary.password"));

        System.out.println("🔄 Reiniciando conexión a la base de datos '" + targetDb + "'...");
        return dataSource;
    }

}
