package com.geovannycode.bookstore.orders;

import org.springframework.modulith.events.Externalized;
import java.math.BigDecimal;
import java.util.List;

/**
 * Evento publicado cuando una orden es creada exitosamente.
 *
 * Al estar en la raíz del módulo orders, es parte de la API pública —
 * cualquier módulo puede suscribirse a él.
 *
 * Ahora lleva una lista de ítems para soportar órdenes multi-producto.
 *
 * @Externalized publica el evento a RabbitMQ además de entregarlo
 * internamente. El formato es: "exchangeName::routingKey"
 */
@Externalized("bookstore.orders::order.created")
public record OrderCreatedEvent(
        String orderNumber,
        List<Item> items,
        Customer customer
) {
    /**
     * Snapshot del producto al momento de la compra.
     * Cada ítem tiene código, nombre, precio y cantidad.
     */
    public record Item(
            String productCode,
            String productName,
            BigDecimal productPrice,
            int quantity
    ) {}

    /**
     * El Customer va embebido para que los consumidores externos
     * no necesiten consultar la aplicación para obtener estos datos.
     */
    public record Customer(
            String name,
            String email,
            String phone,
            String deliveryAddress
    ) {}
}