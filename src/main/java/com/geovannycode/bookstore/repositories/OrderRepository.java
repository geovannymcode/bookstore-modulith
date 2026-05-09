package com.geovannycode.bookstore.repositories;

import com.geovannycode.bookstore.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    Page<OrderEntity> findByCustomerEmail(String email, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);
}
