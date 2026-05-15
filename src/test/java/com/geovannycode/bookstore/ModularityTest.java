package com.geovannycode.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTest {

    // Spring Modulith analiza la estructura de paquetes estáticamente.
    // No necesita levantar el contexto de Spring para hacerlo.
    static final ApplicationModules modules = ApplicationModules.of(BookstoreApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    /**
     * Genera documentación C4 Model en target/spring-modulith-docs/
     *
     * Incluye:
     *   - components.puml: diagrama de componentes global
     *   - catalog.puml, orders.puml, inventory.puml: diagramas por módulo
     *   - aggregating-document.adoc: documento AsciiDoc completo
     *
     * Los .puml se pueden renderizar con PlantUML (plugin para IntelliJ,
     * VS Code, o el servidor online en plantuml.com).
     */
    @Test
    void writesDocumentationSnippets() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeAggregatingDocument();
    }

    @Test
    void printsModuleStructure() {
        modules.forEach(System.out::println);
    }
}
