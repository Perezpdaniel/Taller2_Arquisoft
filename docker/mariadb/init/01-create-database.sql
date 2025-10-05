-- Script de initialization of the database
-- This script is executed automatically when creating the MariaDB container

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS hotel_reservations 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE hotel_reservations;

-- Create a specific user for the application (if it doesn't exist)
CREATE USER IF NOT EXISTS 'hotel_user'@'%' IDENTIFIED BY 'hotel_password';

-- Grant permissions to the user
GRANT ALL PRIVILEGES ON hotel_reservations.* TO 'hotel_user'@'%';

-- Apply the changes
FLUSH PRIVILEGES;

-- Create example tables (optional - JPA can create them automatically)
-- These tables will be created automatically by JPA if they don't exist

-- Table of clients
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table of reservations
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    room_number INT NOT NULL,
    observations TEXT,
    state VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    client_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_room_number (room_number),
    INDEX idx_client_id (client_id)
);

-- Insert example data
INSERT IGNORE INTO clients (name, email, phone) VALUES
('Juan Pérez', 'juan.perez@email.com', '+57 300 123 4567'),
('María García', 'maria.garcia@email.com', '+57 301 234 5678'),
('Carlos López', 'carlos.lopez@email.com', '+57 302 345 6789'),
('Ana Martínez', 'ana.martinez@email.com', '+57 303 456 7890'),
('Luis Rodríguez', 'luis.rodriguez@email.com', '+57 304 567 8901');

-- Insert reservations of example
INSERT IGNORE INTO reservations (start_date, end_date, room_number, observations, state, client_id) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 101, 'Cliente VIP', 'CONFIRMED', 1),
(DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 205, 'Habitación con vista al mar', 'CONFIRMED', 2),
(DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 302, 'Servicio de habitación incluido', 'CONFIRMED', 3),
(DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 17 DAY), 108, 'Check-in temprano solicitado', 'CONFIRMED', 4),
(DATE_ADD(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 22 DAY), 401, 'Suite ejecutiva', 'CONFIRMED', 5);

-- Create additional indexes for better performance
CREATE INDEX IF NOT EXISTS idx_reservations_dates ON reservations(start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_reservations_state ON reservations(state);
CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);

-- Show information of the created tables
SELECT 'Base de datos hotel_reservations inicializada correctamente' AS mensaje;
SELECT COUNT(*) AS total_clients FROM clients;
SELECT COUNT(*) AS total_reservations FROM reservations;