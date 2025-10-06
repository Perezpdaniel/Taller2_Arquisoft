-- Script de creation of schema for JPA/EclipseLink
-- This script is executed when JPA generates the schema automatically

-- Create table of clients
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL
);

-- Create table of reservations
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    room_number INT NOT NULL,
    observations TEXT,
    state VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    client_id BIGINT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_reservations_client ON reservations(client_id);
CREATE INDEX idx_reservations_dates ON reservations(start_date, end_date);
CREATE INDEX idx_reservations_room ON reservations(room_number);
CREATE INDEX idx_reservations_state ON reservations(state);