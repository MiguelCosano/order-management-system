package com.cosano.ordermanagement.orderservice.controller;

import com.cosano.ordermanagement.orderservice.entity.Order;
import com.cosano.ordermanagement.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<String> productIds) {
        return ResponseEntity.ok(orderService.createOrder(productIds));
    }
}