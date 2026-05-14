package com.geovannycode.bookstore.orders.domain;

import com.geovannycode.bookstore.catalog.CatalogApi;
import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CatalogApi catalogApi;                // API pública de catalog
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, CatalogApi catalogApi, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.catalogApi = catalogApi;
        this.eventPublisher = eventPublisher;
    }

    public CreateOrderResponse create(CreateOrderRequest request) {
        // Validar producto via API pública de catalog
        var product = catalogApi.getByCode(request.productCode())
                .orElseThrow(() -> new OrderNotFoundException(request.productCode()));

        // Crear la orden
        var orderNumber = "ORD-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        var order = new OrderEntity(
                orderNumber,
                request.customerName(),
                request.customerEmail(),
                request.customerPhone(),
                request.deliveryAddress(),
                product.code(),
                product.name(),
                product.price(),
                request.quantity()
        );
        var saved = orderRepository.save(order);
        log.info("Orden creada: orderNumber={}, product={}", orderNumber, product.code());

        // Publicar el evento → Spring Modulith lo persiste en event_publication
        // dentro de esta misma transacción
        eventPublisher.publishEvent(new OrderCreatedEvent(
                saved.getOrderNumber(),
                product.code(),
                product.name(),
                product.price(),
                request.quantity(),
                new OrderCreatedEvent.Customer(
                        request.customerName(), request.customerEmail(),
                        request.customerPhone(), request.deliveryAddress()
                )
        ));

        return new CreateOrderResponse(orderNumber);
    }

    @Transactional(readOnly = true)
    public OrderEntity getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }
}
