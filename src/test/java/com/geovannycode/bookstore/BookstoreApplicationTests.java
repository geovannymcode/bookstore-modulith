package com.geovannycode.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Smoke test: verifica que el contexto carga correctamente.
 *
 * Este es el único test que viene con el starter. Durante el workshop
 * vas a agregar:
 *
 * TODO Parte 1: ModularityTest (verificación arquitectural)
 * TODO Parte 5: @ApplicationModuleTest para catalog, orders e inventory
 * TODO Parte 5: AssertablePublishedEvents en el test de orders
 * TODO Parte 5: Scenario en el test de inventory
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
