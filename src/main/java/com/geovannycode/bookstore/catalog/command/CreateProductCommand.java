package com.geovannycode.bookstore.catalog.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Command para crear un producto en el catálogo.
 *
 * En CQRS, los commands representan la intención de cambiar el estado.
 * Llevan las validaciones de entrada para que el servicio reciba
 * datos ya verificados.
 *
 * Es un record público porque ProductRestController lo recibe del HTTP body
 * y lo pasa a CatalogApi. Estar en el sub-paquete command no lo hace privado
 * en términos de Spring Modulith — los types internos son los que no deberían
 * cruzar la frontera del módulo, pero estos commands son parte del API de catalog.
 */
public record CreateProductCommand(
        @NotBlank(message = "El código del producto es obligatorio") String code,

        @NotBlank(message = "El nombre es obligatorio") String name,

        String description,
        String imageUrl,

        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero") BigDecimal price,

        @NotBlank(message = "La categoría es obligatoria") String category) {}
