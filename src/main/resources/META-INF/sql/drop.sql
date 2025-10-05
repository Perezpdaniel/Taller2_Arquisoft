-- Script of drop of schema for JPA/EclipseLink
-- This script is executed when JPA drops the schema automatically

-- Drop tables in reverse order (respecting the foreign keys)
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS clients;