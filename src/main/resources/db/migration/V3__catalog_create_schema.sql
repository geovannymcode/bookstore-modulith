-- V3__catalog_create_schema.sql
-- Crea el schema aislado para el módulo catalog.
-- Cada módulo tendrá su propio schema — eso es la separación de datos
-- que complementa la separación de código que hace Spring Modulith.
CREATE SCHEMA IF NOT EXISTS catalog;