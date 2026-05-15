package com.geovannycode.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Smoke test: verifica que el contexto completo carga sin errores.
 *
 * Si este test pasa, significa que:
 *   ✅ Flyway ejecutó todas las migraciones V1-V6 sin errores
 *   ✅ JPA validó el mapeo de entidades contra el schema real
 *   ✅ Spring Modulith inicializó el Event Publication Registry
 *   ✅ La conexión a Postgres y RabbitMQ está establecida
 *   ✅ Todos los beans se inyectan correctamente
 *
 * Lo ejecutamos como parte del build (./mvnw verify) para detectar
 * problemas de configuración antes de desplegar.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class BookstoreApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no carga, el test falla con el error exacto.
        // Ejecuta este test primero para confirmar que el entorno está listo.
    }
}
