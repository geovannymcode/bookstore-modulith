package com.geovannycode.bookstore.catalog;

import java.math.BigDecimal;

/**
 * DTO público del módulo Catalog.
 *
 * Este record es el contrato del módulo con el exterior.
 * Nunca exponemos ProductEntity ni ProductView directamente —
 * ese es el acoplamiento que queremos evitar.
 *
 * Nótese que tiene averageRating y reviewCount (datos del read model)
 * sin que quien lo consuma sepa que hay dos tablas detrás.
 */
public record Product(
        String code,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        String category,
        double averageRating,
        int reviewCount) {}
