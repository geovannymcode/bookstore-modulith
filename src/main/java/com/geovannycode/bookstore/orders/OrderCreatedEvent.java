package com.geovannycode.bookstore.orders;

import java.math.BigDecimal;

/**
 * Evento publicado cuando se crea una orden.
 *
 * ⚠️ PROBLEMA (para el workshop): este evento vive en models/ junto a
 * CreateOrderRequest, PagedResult y OrderStatus. No tiene ninguna
 * relación conceptual con esos tipos — simplemente están en el mismo
 * paquete técnico.
 *
 * Objetivo: moverlo al paquete raíz del módulo orders/ y anotarlo
 * con @Externalized para que Spring Modulith lo publique a RabbitMQ.
 */
public record OrderCreatedEvent(
        String orderNumber,
        String productCode,
        String productName,
        BigDecimal productPrice,
        int quantity,
        String customerName,
        String customerEmail
) {}
