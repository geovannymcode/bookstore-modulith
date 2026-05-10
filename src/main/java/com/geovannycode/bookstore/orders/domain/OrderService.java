package com.geovannycode.bookstore.orders.domain;

import com.geovannycode.bookstore.catalog.CatalogApi;
import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import com.geovannycode.bookstore.common.models.PagedResult;
import com.geovannycode.bookstore.inventory.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio de órdenes.
 *
 * ⚠️ PROBLEMAS identificados (objetivos del workshop):
 *
 * 1. ACOPLAMIENTO EXCESIVO: inyecta repositorios de Catalog e Inventory
 *    directamente. Una clase de "Orders" no debería saber nada de la
 *    implementación interna de esos dominios.
 *
 * 2. RESPONSABILIDADES MEZCLADAS: valida stock (Inventory), busca
 *    productos (Catalog) y crea la orden (Orders) — todo en el mismo método.
 *
 * 3. EVENTO SIN GARANTÍA: el OrderCreatedEvent se publica con
 *    ApplicationEventPublisher estándar. Si la app cae antes de que
 *    InventoryHandler lo procese, el evento se pierde.
 *
 * 4. TRANSACCIONES CRUZADAS: el descuento de stock ocurre dentro de
 *    la misma transacción que la creación de la orden. Un fallo en
 *    inventory hace rollback de la orden completa.
 *
 * Objetivo: refactorizar para que Orders solo coordine su dominio
 * y se comunique con los demás a través de eventos y APIs públicas.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CatalogApi catalogApi;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        CatalogApi catalogApi,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.catalogApi = catalogApi;
        this.eventPublisher = eventPublisher;
    }

    public CreateOrderResponse create(CreateOrderRequest request) {
        var product = catalogApi.getByCode(request.productCode())
                .orElseThrow(() -> new InvalidOrderException(
                        "Producto no encontrado: " + request.productCode()));

        var orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var order = new OrderEntity(
                orderNumber,
                request.customerName(), request.customerEmail(),
                request.customerPhone(), request.deliveryAddress(),
                product.code(), product.name(),
                product.price(), request.quantity()
        );

        var saved = orderRepository.save(order);
        log.info("Orden creada: {}", orderNumber);

        // Publicamos el evento para que inventory descuente el stock.
        // Nota: en esta parte usamos @EventListener básico.
        // En la Parte 4 lo mejoraremos con @ApplicationModuleListener
        // y el Event Publication Registry (Outbox Pattern).
        eventPublisher.publishEvent(new OrderCreatedEvent(
                saved.getOrderNumber(),
                product.code(),
                product.name(),
                product.price(),
                request.quantity(),
                request.customerName(),
                request.customerEmail()
        ));

        return new CreateOrderResponse(orderNumber);
    }

    @Transactional(readOnly = true)
    public OrderEntity getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }
}
