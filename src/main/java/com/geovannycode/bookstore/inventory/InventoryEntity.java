package com.geovannycode.bookstore.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq")
    @SequenceGenerator(name = "stock_seq", sequenceName = "stock_id_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private int stockLevel;

    protected InventoryEntity() {}

    public InventoryEntity(String productCode, int stockLevel) {
        this.productCode = productCode;
        this.stockLevel = stockLevel;
    }

    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public int getStockLevel() { return stockLevel; }

    /**
     * ⚠️ PROBLEMA (para el workshop): este método modifica el estado
     * pero lo llama OrderService directamente — cruzando dominios.
     * Inventory debería gestionar su propio estado de forma autónoma.
     */
    public void decreaseStock(int quantity) {
        if (this.stockLevel < quantity) {
            throw new IllegalStateException(
                    "Stock insuficiente para " + productCode +
                    ": disponible=" + stockLevel + ", solicitado=" + quantity
            );
        }
        this.stockLevel -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stockLevel += quantity;
    }
}
