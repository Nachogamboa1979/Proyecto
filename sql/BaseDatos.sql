-- Crear la base de datos si no existe (Ejecutar manualmente si es necesario)
-- CREATE DATABASE microservices_db;

-- Usar la base de datos
\c microservices_db;

CREATE DATABASE microservices_db;

\c microservices_db;

CREATE TABLE Persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero CHAR(1) CHECK (genero IN ('M', 'F')),
    edad INT CHECK (edad > 0),
    identificacion VARCHAR(50) UNIQUE NOT NULL,
    direccion TEXT,
    telefono VARCHAR(20)
);

CREATE TABLE Cliente (
    clienteid SERIAL PRIMARY KEY,
    id_persona INT UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_persona) REFERENCES Persona(id) ON DELETE CASCADE
);

CREATE TABLE Cuenta (
    numero_cuenta SERIAL PRIMARY KEY,
    clienteid INT NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL CHECK (saldo_inicial >= 0),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (clienteid) REFERENCES Cliente(clienteid) ON DELETE CASCADE
);

CREATE TABLE Movimiento (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL CHECK (saldo >= 0),
    numero_cuenta INT NOT NULL,
    FOREIGN KEY (numero_cuenta) REFERENCES Cuenta(numero_cuenta) ON DELETE CASCADE
);
