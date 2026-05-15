package com.geovannycode.bookstore.orders.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    Page<OrderEntity> findByCustomerEmail(String email, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);
}
