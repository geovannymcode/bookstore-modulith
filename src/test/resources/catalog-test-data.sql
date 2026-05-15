-- Limpia datos previos
DELETE FROM catalog.product_views WHERE code IN ('P001','P002','P003','P004','P005');
DELETE FROM catalog.products WHERE code IN ('P001','P002','P003','P004','P005');

-- Write model (catalog.products) — para que la validación de duplicados funcione
INSERT INTO catalog.products (code, name, description, image_url, price, category, created_at)
VALUES
    ('P001', 'Clean Code', 'Un manual para crear software ágil.', null, 45.99, 'Ingeniería de Software', now()),
    ('P002', 'The Pragmatic Programmer', 'De novato a maestro.', null, 49.99, 'Ingeniería de Software', now()),
    ('P003', 'Designing Data-Intensive Applications', 'Sistemas de datos a escala.', null, 59.99, 'Sistemas Distribuidos', now()),
    ('P004', 'Domain-Driven Design', 'Tackling complexity.', null, 55.99, 'Arquitectura', now()),
    ('P005', 'Microservices Patterns', 'Con ejemplos en Java.', null, 52.99, 'Arquitectura', now());

-- Read model (catalog.product_views) — para que las consultas funcionen
INSERT INTO catalog.product_views
(code, name, description, image_url, price, category, average_rating, review_count)
VALUES
    ('P001', 'Clean Code', 'Un manual para crear software ágil.', null, 45.99, 'Ingeniería de Software', 4.8, 235),
    ('P002', 'The Pragmatic Programmer', 'De novato a maestro.', null, 49.99, 'Ingeniería de Software', 4.7, 189),
    ('P003', 'Designing Data-Intensive Applications', 'Sistemas de datos a escala.', null, 59.99, 'Sistemas Distribuidos', 4.9, 312),
    ('P004', 'Domain-Driven Design', 'Tackling complexity.', null, 55.99, 'Arquitectura', 4.6, 145),
    ('P005', 'Microservices Patterns', 'Con ejemplos en Java.', null, 52.99, 'Arquitectura', 4.7, 178);