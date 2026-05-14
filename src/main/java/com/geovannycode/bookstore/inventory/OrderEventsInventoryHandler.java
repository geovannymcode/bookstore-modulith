package com.geovannycode.bookstore.inventory;

import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Handler que recibe el evento de orden creada.
 *
 * ⚠️ PROBLEMAS (para el workshop):
 *
 * 1. USA @EventListener: el handler se ejecuta DENTRO de la misma
 *    transacción que creó la orden. Si falla, hace rollback de la
 *    orden completa — comportamiento que puede no ser el deseado.
 *
 * 2. SIN GARANTÍA DE ENTREGA: si la aplicación se reinicia después
 *    del commit pero antes de que este handler ejecute en un escenario
 *    async, el evento se pierde sin dejar rastro.
 *
 * 3. REDUNDANTE: OrderService ya descontó el stock directamente
 *    a través de InventoryRepository. Este handler nunca recibe
 *    el control porque el @EventListener es síncrono y OrderService
 *    ya hizo el trabajo antes.
 *
 * Objetivo: usar @ApplicationModuleListener con Event Publication Registry
 * para garantizar entrega y separar las transacciones correctamente.
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
        log.info("Actualizando stock → order={}, product={}, qty={}",
                event.orderNumber(), event.productCode(), event.quantity());
        inventoryService.decreaseStock(event.productCode(), event.quantity());
    }
}
