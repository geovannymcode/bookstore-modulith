package com.geovannycode.bookstore.orders.domain;

import jakarta.validation.constraints.*;

/**
 * Request para crear una orden.
 *
 * ⚠️ PROBLEMA: este modelo vive en models/ junto a CreateProductRequest,
 * PagedResult, OrderStatus y Customer — tipos de dominios completamente
 * distintos mezclados en el mismo paquete.
 */
public record CreateOrderRequest(
        @NotBlank(message = "El código del producto es obligatorio")
        String productCode,

        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 100, message = "La cantidad máxima por orden es 100")
        int quantity,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        String customerName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String customerEmail,

        @NotBlank(message = "El teléfono es obligatorio")
        String customerPhone,

        @NotBlank(message = "La dirección de entrega es obligatoria")
        String deliveryAddress
) {}
