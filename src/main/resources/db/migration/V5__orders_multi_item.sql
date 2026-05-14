-- ============================================================
-- V5: Soporte multi-ítem en órdenes
--
-- Antes: cada orden tenía un solo producto (product_code, product_name,
-- product_price, quantity directamente en la tabla orders).
--
-- Ahora: una orden puede tener N ítems. Los datos del producto se
-- mueven a la nueva tabla order_items.
-- ============================================================

-- ── Secuencia para order_items ──────────────────────────────
CREATE SEQUENCE order_item_id_seq START WITH 100 INCREMENT BY 50;

-- ── Nueva tabla de ítems ────────────────────────────────────
CREATE TABLE order_items (
    id            BIGINT          NOT NULL DEFAULT nextval('order_item_id_seq'),
    order_id      BIGINT          NOT NULL,
    product_code  VARCHAR(50)     NOT NULL,
    product_name  VARCHAR(255)    NOT NULL,
    product_price NUMERIC(10, 2)  NOT NULL,
    quantity      INT             NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- ── Migrar datos existentes de orders → order_items ─────────
INSERT INTO order_items (id, order_id, product_code, product_name, product_price, quantity)
SELECT nextval('order_item_id_seq'), id, product_code, product_name, product_price, quantity
FROM orders
WHERE product_code IS NOT NULL;

-- ── Eliminar columnas de producto de orders ─────────────────
ALTER TABLE orders DROP COLUMN product_code;
ALTER TABLE orders DROP COLUMN product_name;
ALTER TABLE orders DROP COLUMN product_price;
ALTER TABLE orders DROP COLUMN quantity;

-- ── Eliminar índice que referenciaba la columna eliminada ───
DROP INDEX IF EXISTS idx_orders_product_code;
