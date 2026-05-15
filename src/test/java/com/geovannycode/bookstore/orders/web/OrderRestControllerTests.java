package com.geovannycode.bookstore.orders.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.geovannycode.bookstore.TestcontainersConfiguration;
import com.geovannycode.bookstore.catalog.CatalogApi;
import com.geovannycode.bookstore.catalog.Product;
import com.geovannycode.bookstore.orders.OrderCreatedEvent;
import java.math.BigDecimal;
import java.util.Optional;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test del módulo orders en aislamiento.
 *
 * @MockitoBean CatalogApi: catalog no se carga en STANDALONE.
 * Mockito provee el bean para que OrderService pueda inyectarlo.
 *
 * AssertablePublishedEvents: se inyecta como parámetro del test
 * y captura los eventos publicados durante su ejecución.
 * Permite verificar que orders cumple su contrato de publicación
 * sin necesitar RabbitMQ ni un consumer real.
 */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
class OrderRestControllerTests {

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
    WebApplicationContext context;

    MockMvc mockMvc;

    @MockitoBean
    CatalogApi catalogApi;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        var product = new Product(
                "P001",
                "Clean Code",
                "Un manual de calidad",
                null,
                new BigDecimal("45.99"),
                "Ingeniería de Software",
                4.8,
                235);
        given(catalogApi.getByCode("P001")).willReturn(Optional.of(product));
        given(catalogApi.getByCode("INEXISTENTE")).willReturn(Optional.empty());
    }

    @Test
    void shouldCreateOrderSuccessfully(AssertablePublishedEvents events) throws Exception {
        var request = """
                {
                  "customerName": "Geovanny Mendoza",
                  "customerEmail": "geo@barranquillajug.com",
                  "customerPhone": "+57 300 1234567",
                  "deliveryAddress": "Calle 72 #45-10, Barranquilla",
                  "items": [
                    { "productCode": "P001", "quantity": 2 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber", startsWith("ORD-")));

        var orderEvents = events.ofType(OrderCreatedEvent.class);

        assertThat(orderEvents).isNotEmpty();
        assertThat(orderEvents).anySatisfy(event -> {
            assertThat(event.orderNumber()).startsWith("ORD-");
            assertThat(event.customer().email()).isEqualTo("geo@barranquillajug.com");
            assertThat(event.items()).hasSize(1);
            assertThat(event.items().get(0).productCode()).isEqualTo("P001");
            assertThat(event.items().get(0).quantity()).isEqualTo(2);
        });
    }

    @Test
    void shouldReturn400WhenProductNotFound() throws Exception {
        var request = """
                {
                  "customerName": "Test User",
                  "customerEmail": "test@test.com",
                  "customerPhone": "+57 300 0000000",
                  "deliveryAddress": "Test Address",
                  "items": [
                    { "productCode": "INEXISTENTE", "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForNonExistentOrder() throws Exception {
        mockMvc.perform(get("/api/orders/ORD-NOTFOUND")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidRequest() throws Exception {
        var invalidRequest = """
                {
                  "customerName": "",
                  "customerEmail": "not-an-email",
                  "customerPhone": "",
                  "deliveryAddress": "",
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotPublishEventWhenOrderFails(AssertablePublishedEvents events) throws Exception {
        var request = """
                {
                  "customerName": "Test",
                  "customerEmail": "test@test.com",
                  "customerPhone": "+57 300 0000000",
                  "deliveryAddress": "Test",
                  "items": [
                    { "productCode": "INEXISTENTE", "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertThat(events.ofType(OrderCreatedEvent.class)).isEmpty();
    }
}
