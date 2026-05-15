package com.geovannycode.bookstore.catalog.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovannycode.bookstore.TestcontainersConfiguration;
import com.geovannycode.bookstore.catalog.command.CreateProductCommand;
import java.math.BigDecimal;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test del módulo catalog en aislamiento total.
 *
 * Con @ApplicationModuleTest (STANDALONE), Spring carga:
 *   ✅ catalog.command.*  (ProductEntity, ProductCommandService, ProductRepository)
 *   ✅ catalog.query.*    (ProductView, ProductQueryService, ProductViewRepository)
 *   ✅ catalog.internal.* (CatalogEventHandler)
 *   ✅ catalog.web.*      (ProductRestController, CatalogExceptionHandler)
 *   ✅ common.*           (PagedResult — es OPEN, se incluye)
 *
 *   ❌ orders.*     (no se carga)
 *   ❌ inventory.*  (no se carga)
 *
 * Verifica en los logs que el ApplicationContext tiene muchos menos beans
 * que con @SpringBootTest.
 */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
@Sql("/catalog-test-data.sql")
class ProductRestControllerTests {

    /**
     * Flyway definido aquí como @TestConfiguration para que Spring Modulith
     * no lo rechace. FlywayConfig está en el módulo 'config' y no puede
     * importarse en un test aislado del módulo 'catalog'.
     */
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

    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldReturnProductsPagedResult() throws Exception {
        mockMvc.perform(get("/api/catalog/products").param("page", "1").param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)));
    }

    @Test
    void shouldReturnProductByCode() throws Exception {
        mockMvc.perform(get("/api/catalog/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("P001")))
                .andExpect(jsonPath("$.name", is("Clean Code")))
                .andExpect(jsonPath("$.averageRating", greaterThan(0.0)));
    }

    @Test
    void shouldReturn404WithProblemDetailForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/catalog/products/NONEXISTENT")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnProductsByCategory() throws Exception {
        mockMvc.perform(get("/api/catalog/products/category/Arquitectura"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].category", is("Arquitectura")));
    }

    @Test
    void shouldReturnTopRatedProducts() throws Exception {
        mockMvc.perform(get("/api/catalog/products/top-rated").param("minRating", "4.7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].averageRating", greaterThanOrEqualTo(4.7)));
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        var command = new CreateProductCommand(
                "P999", "Test Book", "Un libro de prueba", null, new BigDecimal("29.99"), "Testing");

        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("P999")))
                .andExpect(jsonPath("$.name", is("Test Book")));
    }

    @Test
    void shouldReturn409WhenCreatingDuplicateProduct() throws Exception {
        var command = new CreateProductCommand(
                "P001", "Duplicado", "Ya existe P001", null, new BigDecimal("10.00"), "Testing");

        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Producto ya existe")));
    }

    @Test
    void shouldReturn400WhenCreatingProductWithInvalidData() throws Exception {
        var invalidJson = """
                {
                  "code": "",
                  "name": "Sin código",
                  "price": -5.00,
                  "category": "Test"
                }
                """;

        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
