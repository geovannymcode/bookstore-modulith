# Bookstore Starter — Proyecto Inicial

> **Workshop: Monolito Modular Real con Spring Modulith - BarranquillaJUG**

Este es el **punto de partida** del workshop. Es una aplicación Spring Boot funcional
con package-by-layer que tiene problemas de acoplamiento que vas a identificar
y refactorizar durante el taller.

---

## Estructura Actual (Package-by-Layer)

```
com.geovannycode.bookstore/
├── config/
│   ├── GlobalExceptionHandler.java   ← conoce todos los dominios
│   └── RabbitMQConfig.java
├── entities/
│   ├── ProductEntity.java            ← dominio Catalog
│   ├── OrderEntity.java              ← dominio Orders
│   └── InventoryEntity.java          ← dominio Inventory
├── exceptions/
│   ├── ProductNotFoundException.java
│   ├── ProductAlreadyExistsException.java
│   ├── OrderNotFoundException.java
│   └── InvalidOrderException.java
├── models/
│   ├── CreateProductRequest.java
│   ├── CreateOrderRequest.java
│   ├── CreateOrderResponse.java
│   ├── OrderCreatedEvent.java
│   ├── OrderStatus.java
│   └── PagedResult.java
├── repositories/
│   ├── ProductRepository.java        ← accesible desde cualquier clase
│   ├── OrderRepository.java
│   └── InventoryRepository.java
├── services/
│   ├── ProductService.java
│   ├── OrderService.java             ← ⚠️ inyecta ProductRepository e InventoryRepository
│   ├── InventoryService.java
│   └── OrderEventsInventoryHandler.java
└── web/
    ├── ProductRestController.java
    └── OrderRestController.java
```

## Problemas a Identificar

Antes de empezar a refactorizar, lee el código y responde:

1. ¿Cuántos repositorios inyecta `OrderService`? ¿De qué dominios son?
2. ¿Qué hace `OrderEventsInventoryHandler` actualmente? ¿Es letra muerta?
3. ¿Cuántas excepciones de dominios distintos maneja `GlobalExceptionHandler`?
4. ¿Por qué `OrderCreatedEvent` está en el paquete `models/`?
5. Si extraes `Catalog` como microservicio, ¿qué clases tendrías que mover?

---

## Inicio Rápido

### Prerrequisitos
- Java 21+
- Docker Desktop corriendo
- Maven 3.9+ (o usar `./mvnw`)

### Levantar la infraestructura

```bash
docker compose up -d
```

### Verificar que compila y el contexto carga

```bash
./mvnw test -Dtest=BookstoreApplicationTests
```

### Arrancar la aplicación

```bash
./mvnw spring-boot:run
```

### Probar los endpoints

```bash
# Listar productos
curl http://localhost:8080/api/catalog/products

# Crear una orden (observa los logs de OrderService)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P001",
    "quantity": 2,
    "customerName": "Geovanny Mendoza",
    "customerEmail": "geo@jugbaq.com",
    "customerPhone": "+57 300 1234567",
    "deliveryAddress": "Calle 72 #45-10, Barranquilla"
  }'
```

---

## Tu Progreso en el Workshop

### Parte 0 — Entender el problema
- [ ] Identificar las dependencias cruzadas en `OrderService`
- [ ] Ver que `OrderEventsInventoryHandler` es letra muerta
- [ ] Contar las responsabilidades de `GlobalExceptionHandler`

### Parte 1 — Migración + Spring Modulith
- [ ] Reorganizar a package-by-feature (`catalog/`, `orders/`, `inventory/`, `common/`)
- [ ] Agregar `spring-modulith-starter-core` al `pom.xml`
- [ ] Crear `ModularityTest` y ejecutarlo (va a fallar)
- [ ] Crear `common/package-info.java` con `@ApplicationModule(type = OPEN)`
- [ ] Crear `CatalogApi` como API pública de catalog
- [ ] Actualizar `OrderService` para usar `CatalogApi`
- [ ] Mover `OrderCreatedEvent` a la raíz de `orders/`
- [ ] `ModularityTest` pasa ✅

### Parte 2 — Boundaries avanzados
- [ ] Agregar `orders/package-info.java` con `allowedDependencies = {"catalog"}`
- [ ] Demostrar violación circular y resolverla
- [ ] `ModularityTest` pasa con todas las reglas ✅

### Parte 3 — CQRS en Catalog
- [ ] Crear `catalog.products` (write) y `catalog.product_views` (read) en Flyway
- [ ] Implementar `ProductEntity` (command) y `ProductView` (query)
- [ ] Implementar `CatalogEvents` con `sealed interface`
- [ ] Implementar `CatalogEventHandler` con `@ApplicationModuleListener`

### Parte 4 — Eventos y Outbox
- [ ] Agregar `spring-modulith-starter-jdbc` y `spring-modulith-events-amqp`
- [ ] Agregar `@Externalized` a `OrderCreatedEvent`
- [ ] Actualizar `OrderEventsInventoryHandler` a `@ApplicationModuleListener`
- [ ] Verificar `event_publication` en PostgreSQL y mensajes en RabbitMQ

### Parte 5 — Testing en aislamiento
- [ ] Crear `@ApplicationModuleTest` para catalog
- [ ] Crear `@ApplicationModuleTest` + `@MockitoBean CatalogApi` para orders
- [ ] Verificar eventos con `AssertablePublishedEvents`
- [ ] Crear test con `Scenario` para inventory

### Parte 6 — C4 Docs y Observabilidad
- [ ] Agregar `writesDocumentationSnippets()` a `ModularityTest`
- [ ] Verificar `/actuator/modulith`
- [ ] Ver traza completa en Zipkin

---

El proyecto final de referencia está en `bookstore-modulith/`.
Úsalo solo cuando te quedes atascado, construir el camino es el aprendizaje.

**Guía completa**: [geolabs-spring-modulith.github.io](https://geolabs-spring-modulith.github.io)
