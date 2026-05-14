package com.geovannycode.bookstore.orders.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Request para crear una orden con múltiples ítems.
 *
 * Cada ítem referencia un producto del catálogo por su código
 * y la cantidad deseada. Los datos del cliente van en el nivel raíz.
 */
public record CreateOrderRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio")
        String customerName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String customerEmail,

        @NotBlank(message = "El teléfono es obligatorio")
        String customerPhone,

        @NotBlank(message = "La dirección de entrega es obligatoria")
        String deliveryAddress,

        @NotEmpty(message = "La orden debe tener al menos un ítem")
        @Valid
        List<Item> items
) {
    /**
     * Representa un ítem del request: qué producto y cuántas unidades.
     */
    public record Item(
            @NotBlank(message = "El código del producto es obligatorio")
            String productCode,

            @Min(value = 1, message = "La cantidad mínima es 1")
            @Max(value = 100, message = "La cantidad máxima por ítem es 100")
            int quantity
    ) {}
}
