package com.geovannycode.bookstore.catalog.command;

import com.geovannycode.bookstore.catalog.Product;
import com.geovannycode.bookstore.catalog.internal.CatalogEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de escritura del módulo Catalog (Command side).
 *
 * Responsabilidades:
 * 1. Validar y persistir el cambio en el write model (ProductEntity)
 * 2. Publicar el evento que sincronizará el read model
 *
 * Nótese que este servicio NO actualiza ProductView directamente.
 * Eso es responsabilidad del Query side, que reacciona al evento.
 */
@Service
public class ProductCommandService {

    private static final Logger log = LoggerFactory.getLogger(ProductCommandService.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    ProductCommandService(ProductRepository productRepository,
                          ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Product create(CreateProductCommand cmd) {
        if (productRepository.existsByCode(cmd.code())) {
            throw new ProductAlreadyExistsException(cmd.code());
        }

        var entity = new ProductEntity(
                cmd.code(), cmd.name(), cmd.description(),
                cmd.imageUrl(), cmd.price(), cmd.category()
        );
        var saved = productRepository.save(entity);
        log.info("Producto creado en write model: code={}", saved.getCode());

        // Publicamos el evento dentro de la transacción.
        // CatalogEventHandler lo recibirá después del commit y actualizará
        // product_views en su propia transacción independiente.
        eventPublisher.publishEvent(new CatalogEvents.ProductCreated(
                saved.getCode(), saved.getName(), saved.getDescription(),
                saved.getImageUrl(), saved.getPrice(), saved.getCategory()
        ));

        return toProduct(saved);
    }

    @Transactional
    public Product update(String code, UpdateProductCommand cmd) {
        var entity = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));

        entity.setName(cmd.name());
        entity.setDescription(cmd.description());
        entity.setImageUrl(cmd.imageUrl());
        entity.setPrice(cmd.price());
        entity.setCategory(cmd.category());

        var saved = productRepository.save(entity);
        log.info("Producto actualizado en write model: code={}", saved.getCode());

        eventPublisher.publishEvent(new CatalogEvents.ProductUpdated(
                saved.getCode(), saved.getName(), saved.getDescription(),
                saved.getImageUrl(), saved.getPrice(), saved.getCategory()
        ));

        return toProduct(saved);
    }

    public Optional<ProductEntity> findEntityByCode(String code) {
        return productRepository.findByCode(code);
    }

    public List<ProductEntity> findEntitiesByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    private Product toProduct(ProductEntity e) {
        // En el command side devolvemos rating 0.0 porque el read model
        // aún no procesó el evento. El cliente debería hacer un GET
        // para obtener el producto con rating actualizado.
        return new Product(
                e.getCode(), e.getName(), e.getDescription(),
                e.getImageUrl(), e.getPrice(), e.getCategory(),
                0.0, 0
        );
    }
}