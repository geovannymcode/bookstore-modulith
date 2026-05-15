package com.geovannycode.bookstore.catalog.command;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "El código es obligatorio") String code,

        @NotBlank(message = "El nombre es obligatorio") String name,

        String description,
        String imageUrl,

        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero") BigDecimal price,

        @NotBlank(message = "La categoría es obligatoria") String category) {}
