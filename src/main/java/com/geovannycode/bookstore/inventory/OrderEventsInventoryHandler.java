package com.geovannycode.bookstore.inventory;

import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Handler que recibe el evento de orden creada y descuenta stock.
 *
 * Usa @ApplicationModuleListener (= @Async + @TransactionalEventListener
 * + @Transactional(REQUIRES_NEW)) para que:
 * 1. Se ejecute DESPUÉS del commit de la orden (post-commit)
 * 2. Corra en un hilo separado (async)
 * 3. Tenga su propia transacción independiente
 *
 * Si falla, el Event Publication Registry reintentará automáticamente.
 *
 * Ahora itera sobre cada ítem de la orden para descontar stock
 * de múltiples productos.
 */
@Component
public class OrderEventsInventoryHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsInventoryHandler.class);

    private final InventoryService inventoryService;

    public OrderEventsInventoryHandler(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @ApplicationModuleListener
    public void on(OrderCreatedEvent event) {
        for (OrderCreatedEvent.Item item : event.items()) {
            log.info("Actualizando stock → order={}, product={}, qty={}",
                    event.orderNumber(), item.productCode(), item.quantity());
            inventoryService.decreaseStock(item.productCode(), item.quantity());
        }
    }
}
