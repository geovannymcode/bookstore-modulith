package com.geovannycode.bookstore.services;

import com.geovannycode.bookstore.entities.InventoryEntity;
import com.geovannycode.bookstore.repositories.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de inventario.
 *
 * ⚠️ PROBLEMA (para el workshop): aunque este servicio existe, OrderService
 * no lo usa — en cambio, inyecta InventoryRepository directamente y llama
 * a InventoryEntity.decreaseStock() él mismo. Esta clase ni siquiera es
 * llamada en el flujo de creación de órdenes del starter.
 *
 * Objetivo: que Orders NUNCA toque InventoryRepository directamente.
 * Inventory debe ser soberano de su propio estado y reaccionar a eventos.
 */
@Service
@Transactional
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void decreaseStock(String productCode, int quantity) {
        var stock = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalStateException(
                        "Sin stock para: " + productCode));

        stock.decreaseStock(quantity);
        inventoryRepository.save(stock);
        log.info("Stock disminuido: product={}, qty={}, remaining={}",
                productCode, quantity, stock.getStockLevel());
    }

    public void increaseStock(String productCode, int quantity) {
        var stock = inventoryRepository.findByProductCode(productCode)
                .orElseGet(() -> new InventoryEntity(productCode, 0));

        stock.increaseStock(quantity);
        inventoryRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public int getStockLevel(String productCode) {
        return inventoryRepository.findByProductCode(productCode)
                .map(InventoryEntity::getStockLevel)
                .orElse(0);
    }
}
