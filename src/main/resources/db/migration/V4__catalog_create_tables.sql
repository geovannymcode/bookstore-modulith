-- V4__catalog_create_tables.sql
SET search_path TO catalog;

CREATE SEQUENCE product_id_seq START WITH 100 INCREMENT BY 50;

-- ── Write model (Command side) ────────────────────────────────────────
-- Modelo normalizado, optimizado para consistencia de escritura.
-- Solo ProductCommandService escribe aquí.
CREATE TABLE products (
                          id          BIGINT          NOT NULL DEFAULT nextval('catalog.product_id_seq'),
                          code        VARCHAR(50)     NOT NULL UNIQUE,
                          name        VARCHAR(255)    NOT NULL,
                          description TEXT,
                          image_url   VARCHAR(500),
                          price       NUMERIC(10, 2)  NOT NULL CHECK (price > 0),
                          category    VARCHAR(100)    NOT NULL,
                          created_at  TIMESTAMP       NOT NULL DEFAULT now(),
                          updated_at  TIMESTAMP,
                          PRIMARY KEY (id)
);

-- ── Read model (Query side) ───────────────────────────────────────────
-- Modelo desnormalizado, optimizado para consultas frecuentes.
-- Se sincroniza con el write model a través de eventos internos.
-- average_rating y review_count están cacheados — sin JOINs en consulta.
CREATE TABLE product_views (
                               code            VARCHAR(50)      NOT NULL,
                               name            VARCHAR(255)     NOT NULL,
                               description     TEXT,
                               image_url       VARCHAR(500),
                               price           NUMERIC(10, 2)   NOT NULL,
                               category        VARCHAR(100)     NOT NULL,
                               average_rating  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                               review_count    INT              NOT NULL DEFAULT 0,
                               last_updated_at TIMESTAMP        NOT NULL DEFAULT now(),
                               PRIMARY KEY (code)
);

CREATE INDEX idx_product_views_category ON product_views(category);
CREATE INDEX idx_product_views_rating   ON product_views(average_rating DESC);