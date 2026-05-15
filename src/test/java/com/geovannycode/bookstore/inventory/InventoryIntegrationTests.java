package com.geovannycode.bookstore.inventory;

import com.geovannycode.bookstore.TestcontainersConfiguration;
import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test del módulo inventory en aislamiento usando Scenario.
 *
 * Scenario publica el evento directamente — el módulo orders no se carga.
 * Esto testea exactamente la responsabilidad de inventory:
 * "dado este evento, ¿se descuenta el stock correctamente?"
 */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
class InventoryIntegrationTests {

    @TestConfiguration
    static class FlywayTestConfig {
        @Bean(initMethod = "migrate")
        Flyway flyway(DataSource dataSource) {
            return Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();
        }
    }

    @Autowired
    InventoryService inventoryService;

    @Test
    void shouldDecreaseStockWhenOrderCreatedEventReceived(Scenario scenario) {
        int stockBefore = inventoryService.getStockLevel("P001");
        assertThat(stockBefore).isGreaterThan(0);

        var event = new OrderCreatedEvent(
                "ORD-TEST-001",
                List.of(
                        new OrderCreatedEvent.Item("P001", "Clean Code", new BigDecimal("45.99"), 3)
                ),
                new OrderCreatedEvent.Customer(
                        "Test User", "test@test.com",
                        "+57 300 0000000", "Test Address"
                )
        );

        scenario.publish(event)
                .andWaitForStateChange(
                        () -> inventoryService.getStockLevel("P001")
                )
                .andVerify(newStockLevel ->
                        assertThat(newStockLevel).isEqualTo(stockBefore - 3)
                );
    }

    @Test
    void shouldHandleMultipleItemsInOrder(Scenario scenario) {
        int stockP002Before = inventoryService.getStockLevel("P002");
        int stockP003Before = inventoryService.getStockLevel("P003");

        var event = new OrderCreatedEvent(
                "ORD-TEST-002",
                List.of(
                        new OrderCreatedEvent.Item("P002", "The Pragmatic Programmer", new BigDecimal("49.99"), 5),
                        new OrderCreatedEvent.Item("P003", "Designing Data-Intensive Applications", new BigDecimal("59.99"), 2)
                ),
                new OrderCreatedEvent.Customer(
                        "Bulk Buyer", "bulk@test.com",
                        "+57 311 0000000", "Warehouse"
                )
        );

        scenario.publish(event)
                .andWaitForStateChange(
                        () -> inventoryService.getStockLevel("P002")
                )
                .andVerify(newStockP002 -> {
                    assertThat(newStockP002).isEqualTo(stockP002Before - 5);
                    assertThat(inventoryService.getStockLevel("P003"))
                            .isEqualTo(stockP003Before - 2);
                });
    }

    @Test
    void shouldGetCurrentStockLevel() {
        int stock = inventoryService.getStockLevel("P003");
        assertThat(stock).isGreaterThan(0);
    }
}