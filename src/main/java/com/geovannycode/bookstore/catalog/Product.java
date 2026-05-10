package com.geovannycode.bookstore.catalog;

import java.math.BigDecimal;

/**
 * DTO público del módulo catalog.
 *
 * Este record es el contrato de catalog con el resto del sistema.
 * Es lo único que otros módulos saben sobre un producto.
 * ProductEntity (con su ID de base de datos, timestamps, etc.)
 * permanece privado dentro de catalog/command/.
 *
 * En la Parte 3, cuando implementemos CQRS, este DTO también
 * incluirá averageRating y reviewCount del read model.
 */
public record Product(
        String code,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        String category
) {}