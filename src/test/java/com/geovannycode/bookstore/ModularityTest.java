package com.geovannycode.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    // Spring Modulith analiza la estructura de paquetes estáticamente.
    // No necesita levantar el contexto de Spring para hacerlo.
    static final ApplicationModules modules =
            ApplicationModules.of(BookstoreApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void printsModuleStructure() {
        modules.forEach(System.out::println);
    }
}
