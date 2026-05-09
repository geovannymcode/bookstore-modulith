package com.geovannycode.bookstore.services;

import com.geovannycode.bookstore.exceptions.InvalidOrderException;
import com.geovannycode.bookstore.exceptions.ProductNotFoundException;
import com.geovannycode.bookstore.entities.OrderEntity;
import com.geovannycode.bookstore.models.CreateOrderRequest;
import com.geovannycode.bookstore.models.CreateOrderResponse;
import com.geovannycode.bookstore.models.OrderCreatedEvent;
import com.geovannycode.bookstore.models.PagedResult;
import com.geovannycode.bookstore.repositories.InventoryRepository;
import com.geovannycode.bookstore.repositories.OrderRepository;
import com.geovannycode.bookstore.repositories.ProductRepository;
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

    // ⚠️ PROBLEMA: tres repositorios de dominios distintos en un solo servicio
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;       // ← dominio Catalog
    private final InventoryRepository inventoryRepository;   // ← dominio Inventory
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    public CreateOrderResponse create(CreateOrderRequest request) {
        // ⚠️ PROBLEMA: lógica de Catalog mezclada en Orders
        var product = productRepository.findByCode(request.productCode())
                .orElseThrow(() -> new ProductNotFoundException(request.productCode()));

        // ⚠️ PROBLEMA: lógica de Inventory mezclada en Orders
        var stock = inventoryRepository.findByProductCode(request.productCode())
                .orElseThrow(() -> new InvalidOrderException(
                        "Sin registro de stock para: " + request.productCode()));

        if (stock.getStockLevel() < request.quantity()) {
            throw new InvalidOrderException(
                    "Stock insuficiente para " + request.productCode() +
                    ": disponible=" + stock.getStockLevel() +
                    ", solicitado=" + request.quantity());
        }

        // ⚠️ PROBLEMA: Orders modifica el estado de Inventory directamente
        stock.decreaseStock(request.quantity());
        inventoryRepository.save(stock);

        var orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var order = new OrderEntity(
                orderNumber,
                request.customerName(), request.customerEmail(),
                request.customerPhone(), request.deliveryAddress(),
                product.getCode(), product.getName(),
                product.getPrice(), request.quantity()
        );

        var saved = orderRepository.save(order);
        log.info("Orden creada: {}", orderNumber);

        // ⚠️ PROBLEMA: evento sin garantía de entrega — si la app cae
        // antes de que llegue al handler, se pierde permanentemente
        eventPublisher.publishEvent(new OrderCreatedEvent(
                saved.getOrderNumber(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                request.quantity(),
                request.customerName(),
                request.customerEmail()
        ));

        return new CreateOrderResponse(orderNumber);
    }

    @Transactional(readOnly = true)
    public PagedResult<OrderEntity> getAll(int page, int size) {
        var pageable = PageRequest.of(
                Math.max(0, page - 1), size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return PagedResult.of(orderRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public OrderEntity getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new com.geovannycode.bookstore.exceptions.OrderNotFoundException(orderNumber));
    }
}
