package com.cosano.ordermanagement.orderservice.service;

import com.cosano.ordermanagement.orderservice.client.ProductClient;
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

    public Order createOrder(List<String> productIds) {

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

        return orderRepository.save(order);
    }
}