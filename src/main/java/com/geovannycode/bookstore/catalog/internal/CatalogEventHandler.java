package com.geovannycode.bookstore.catalog.internal;

import com.geovannycode.bookstore.catalog.query.ProductView;
import com.geovannycode.bookstore.catalog.query.ProductViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sincronización CQRS: mantiene product_views actualizado.
 *
 * El orden de ejecución es:
 * 1. ProductCommandService.create() guarda en catalog.products y publica el evento
 * 2. La transacción del command hace COMMIT
 * 3. @TransactionalEventListener recibe el evento (post-commit)
 * 4. @Async lo ejecuta en un thread separado
 * 5. @Transactional(REQUIRES_NEW) abre su propia transacción
 * 6. ProductView se crea en catalog.product_views y hace COMMIT
 *
 * Si el paso 6 falla, el paso 1 ya está persistido — no hay rollback.
 * En la Parte 4 agregaremos el Event Publication Registry para garantizar
 * que el reintento ocurra automáticamente en caso de fallo.
 */
@Component
class CatalogEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CatalogEventHandler.class);

    private final ProductViewRepository viewRepository;

    CatalogEventHandler(ProductViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void on(CatalogEvents.ProductCreated event) {
        log.info("CQRS sync → creando ProductView: code={}", event.code());

        var view = new ProductView(
                event.code(), event.name(), event.description(),
                event.imageUrl(), event.price(), event.category()
        );
        viewRepository.save(view);
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void on(CatalogEvents.ProductUpdated event) {
        log.info("CQRS sync → actualizando ProductView: code={}", event.code());

        viewRepository.findByCode(event.code()).ifPresent(view -> {
            view.updateFrom(event.name(), event.description(),
                    event.imageUrl(), event.price(), event.category());
            viewRepository.save(view);
        });
    }
}