-- ============================================================
-- V2: Datos iniciales — libros técnicos y stock
-- ============================================================

-- ── Catálogo de libros ────────────────────────────────────────
INSERT INTO products (code, name, description, image_url, price, category) VALUES
('P001', 'Clean Code',
 'Un manual para crear software ágil y de calidad.',
 'https://m.media-amazon.com/images/I/41jEbK-jG+L.jpg',
 45.99, 'Ingeniería de Software'),

('P002', 'The Pragmatic Programmer',
 'De novato a maestro del software, con pragmatismo.',
 'https://m.media-amazon.com/images/I/71f743sOPoL.jpg',
 49.99, 'Ingeniería de Software'),

('P003', 'Designing Data-Intensive Applications',
 'Principios y paradigmas para sistemas de datos a escala.',
 'https://m.media-amazon.com/images/I/91YWFA4V6+L.jpg',
 59.99, 'Sistemas Distribuidos'),

('P004', 'Domain-Driven Design',
 'Tackling complexity in the heart of software.',
 'https://m.media-amazon.com/images/I/71hF6prEdPL.jpg',
 55.99, 'Arquitectura'),

('P005', 'Microservices Patterns',
 'Con ejemplos en Java. El libro de Chris Richardson.',
 'https://m.media-amazon.com/images/I/91QKXZ38YhL.jpg',
 52.99, 'Arquitectura'),

('P006', 'Spring Boot in Action',
 'Desarrolla aplicaciones Spring modernas de forma práctica.',
 null, 39.99, 'Spring Framework'),

('P007', 'Kotlin in Action',
 'Una guía completa para el lenguaje Kotlin moderno.',
 null, 44.99, 'Lenguajes'),

('P008', 'Building Microservices',
 'Designing fine-grained systems. Segunda edición.',
 null, 57.99, 'Arquitectura'),

('P009', 'Release It!',
 'Design and Deploy Production-Ready Software.',
 null, 47.99, 'Ingeniería de Software'),

('P010', 'Fundamentals of Software Architecture',
 'Una guía completa de patrones y principios de arquitectura.',
 null, 53.99, 'Arquitectura');

-- ── Órdenes de ejemplo ───────────────────────────────────────
INSERT INTO orders (order_number, customer_name, customer_email, customer_phone,
                    delivery_address, product_code, product_name, product_price,
                    quantity, status)
VALUES
('ORD-A1B2C3D4', 'Geovanny Mendoza', 'geo@jugbaq.com', '+57 300 1234567',
 'Calle 72 #45-10, Barranquilla', 'P001', 'Clean Code', 45.99, 2, 'DELIVERED'),

('ORD-E5F6G7H8', 'Laura Pérez', 'laura@dev.co', '+57 311 9876543',
 'Carrera 50 #80-20, Barranquilla', 'P003', 'Designing Data-Intensive Applications', 59.99, 1, 'SHIPPED');

-- ── Stock inicial ─────────────────────────────────────────────
INSERT INTO stock (product_code, stock_level) VALUES
('P001', 598), ('P002', 750), ('P003', 299),
('P004', 400), ('P005', 250), ('P006', 500),
('P007', 450), ('P008', 300), ('P009', 350),
('P010', 275);
