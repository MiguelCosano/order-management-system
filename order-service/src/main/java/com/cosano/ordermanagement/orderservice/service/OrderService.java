package com.cosano.ordermanagement.orderservice.service;

import com.cosano.ordermanagement.orderservice.client.ProductClient;
import com.cosano.ordermanagement.orderservice.dto.OrderResponse;
import com.cosano.ordermanagement.orderservice.dto.ProductResponse;
import com.cosano.ordermanagement.orderservice.entity.Order;
import com.cosano.ordermanagement.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public OrderResponse createOrder(List<String> productIds) {

        double total = 0;

        for (String id : productIds) {

            ProductResponse product = productClient.getProductById(id);

            if (!product.getActive()) {
                throw new RuntimeException("Product inactive: " + id);
            }

            total += product.getPrice();
        }

        Order order = Order.builder()
                .productIds(productIds)
                .totalAmount(total)
                .status("CREATED")
                .build();

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productIds(order.getProductIds())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build();
    }
}