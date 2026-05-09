-- ============================================================
-- V1: Tablas del monolito acoplado — un solo schema public
--
-- ⚠️ PROBLEMA (para el workshop): todas las tablas comparten el
-- mismo schema "public". No hay separación de datos por dominio.
-- Si extraes Catalog como microservicio, tendrás que migrar
-- datos mezclados sin saber exactamente cuáles pertenecen a quién.
--
-- Objetivo: migrar a schemas separados (catalog, orders, inventory)
-- donde cada módulo sea dueño exclusivo de sus tablas.
-- ============================================================

-- ── Secuencias ───────────────────────────────────────────────
CREATE SEQUENCE product_id_seq START WITH 100 INCREMENT BY 50;
CREATE SEQUENCE order_id_seq   START WITH 100 INCREMENT BY 50;
CREATE SEQUENCE stock_id_seq   START WITH 100 INCREMENT BY 50;

-- ── Tabla de productos (dominio Catalog) ──────────────────────
CREATE TABLE products (
    id          BIGINT          NOT NULL DEFAULT nextval('product_id_seq'),
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

-- ── Tabla de órdenes (dominio Orders) ────────────────────────
CREATE TABLE orders (
    id               BIGINT          NOT NULL DEFAULT nextval('order_id_seq'),
    order_number     VARCHAR(50)     NOT NULL UNIQUE,
    customer_name    VARCHAR(255)    NOT NULL,
    customer_email   VARCHAR(255)    NOT NULL,
    customer_phone   VARCHAR(50)     NOT NULL,
    delivery_address TEXT            NOT NULL,
    product_code     VARCHAR(50)     NOT NULL,
    product_name     VARCHAR(255)    NOT NULL,
    product_price    NUMERIC(10, 2)  NOT NULL,
    quantity         INT             NOT NULL CHECK (quantity > 0),
    status           VARCHAR(50)     NOT NULL DEFAULT 'NEW',
    created_at       TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_orders_customer_email ON orders(customer_email);
CREATE INDEX idx_orders_product_code   ON orders(product_code);
CREATE INDEX idx_orders_status         ON orders(status);

-- ── Tabla de stock (dominio Inventory) ────────────────────────
CREATE TABLE stock (
    id           BIGINT      NOT NULL DEFAULT nextval('stock_id_seq'),
    product_code VARCHAR(50) NOT NULL UNIQUE,
    stock_level  INT         NOT NULL DEFAULT 0 CHECK (stock_level >= 0),
    PRIMARY KEY (id)
);
