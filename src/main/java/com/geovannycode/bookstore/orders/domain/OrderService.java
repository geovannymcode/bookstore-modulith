package com.geovannycode.bookstore.orders.domain;

import com.geovannycode.bookstore.catalog.CatalogApi;
import com.geovannycode.bookstore.catalog.Product;
import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CatalogApi catalogApi;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(
            OrderRepository orderRepository, CatalogApi catalogApi, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.catalogApi = catalogApi;
        this.eventPublisher = eventPublisher;
    }

    public CreateOrderResponse create(CreateOrderRequest request) {
        // Validar cada producto via API pública de catalog y construir los ítems
        List<OrderItemEntity> orderItems = new ArrayList<>();
        List<OrderCreatedEvent.Item> eventItems = new ArrayList<>();

        for (CreateOrderRequest.Item item : request.items()) {
            Product product = catalogApi
                    .getByCode(item.productCode())
                    .orElseThrow(() -> new InvalidOrderException("Producto no encontrado: " + item.productCode()));

            orderItems.add(new OrderItemEntity(product.code(), product.name(), product.price(), item.quantity()));

            eventItems.add(
                    new OrderCreatedEvent.Item(product.code(), product.name(), product.price(), item.quantity()));
        }

        // Crear la orden con todos los ítems
        var orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var order = new OrderEntity(
                orderNumber,
                request.customerName(),
                request.customerEmail(),
                request.customerPhone(),
                request.deliveryAddress(),
                orderItems);
        var saved = orderRepository.save(order);
        log.info("Orden creada: orderNumber={}, items={}", orderNumber, orderItems.size());

        // Publicar el evento → Spring Modulith lo persiste en event_publication
        // dentro de esta misma transacción
        eventPublisher.publishEvent(new OrderCreatedEvent(
                saved.getOrderNumber(),
                eventItems,
                new OrderCreatedEvent.Customer(
                        request.customerName(), request.customerEmail(),
                        request.customerPhone(), request.deliveryAddress())));

        return new CreateOrderResponse(orderNumber);
    }

    @Transactional(readOnly = true)
    public OrderEntity getByOrderNumber(String orderNumber) {
        return orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }
}
