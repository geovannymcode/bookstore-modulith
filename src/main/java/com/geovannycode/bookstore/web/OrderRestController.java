package com.geovannycode.bookstore.web;

import com.geovannycode.bookstore.entities.OrderEntity;
import com.geovannycode.bookstore.models.CreateOrderRequest;
import com.geovannycode.bookstore.models.CreateOrderResponse;
import com.geovannycode.bookstore.models.PagedResult;
import com.geovannycode.bookstore.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{orderNumber}")
    OrderEntity getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber);
    }

    @GetMapping
    PagedResult<OrderEntity> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAll(page, size);
    }
}
