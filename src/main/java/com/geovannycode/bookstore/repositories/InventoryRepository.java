package com.geovannycode.bookstore.repositories;

import com.geovannycode.bookstore.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByProductCode(String productCode);
}
