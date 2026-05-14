package com.geovannycode.bookstore.orders;

import org.springframework.modulith.events.Externalized;
import java.math.BigDecimal;

/**
 * Evento publicado cuando una orden es creada exitosamente.
 *
 * Al estar en la raíz del módulo orders, es parte de la API pública —
 * cualquier módulo puede suscribirse a él.
 *
 * @Externalized publica el evento a RabbitMQ además de entregarlo
 * internamente. El formato es: "exchangeName::routingKey"
 */
@Externalized("bookstore.orders::order.created")
public record OrderCreatedEvent(
        String orderNumber,
        String productCode,
        String productName,
        BigDecimal productPrice,
        int quantity,
        Customer customer
) {
    // El Customer va embebido para que los consumidores externos
    // no necesiten consultar la aplicación para obtener estos datos.
    public record Customer(
            String name,
            String email,
            String phone,
            String deliveryAddress
    ) {}
}