package com.geovannycode.bookstore.catalog.internal;

import java.math.BigDecimal;

/**
 * Eventos internos del módulo Catalog.
 *
 * Al vivir en catalog.internal (sub-paquete privado), estos eventos
 * NO son parte de la API pública del módulo. Solo CatalogEventHandler
 * los consume — y lo hace dentro del mismo módulo.
 *
 * Usamos sealed interface para que el compilador garantice que manejamos
 * todos los casos en el event handler (exhaustiveness checking).
 */
public sealed interface CatalogEvents {

    /**
     * Publicado por ProductCommandService cuando se crea un producto.
     * El Query side crea el ProductView correspondiente.
     */
    record ProductCreated(
            String code,
            String name,
            String description,
            String imageUrl,
            BigDecimal price,
            String category
    ) implements CatalogEvents {}

    /**
     * Publicado por ProductCommandService cuando se actualiza un producto.
     * El Query side actualiza el ProductView.
     */
    record ProductUpdated(
            String code,
            String name,
            String description,
            String imageUrl,
            BigDecimal price,
            String category
    ) implements CatalogEvents {}
}