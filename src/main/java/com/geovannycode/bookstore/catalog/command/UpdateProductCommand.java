package com.geovannycode.bookstore.catalog.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Command para actualizar un producto existente.
 *
 * No incluye el code porque ese viene como PathVariable en el endpoint.
 * Solo los campos modificables — el code es inmutable una vez creado.
 */
public record UpdateProductCommand(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String description,
        String imageUrl,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        BigDecimal price,

        @NotBlank(message = "La categoría es obligatoria")
        String category
) {}