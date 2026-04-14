package com.cosano.ordermanagement.orderservice.integration;

import com.cosano.ordermanagement.orderservice.entity.Order;
import com.cosano.ordermanagement.orderservice.entity.OrderStatus;
import com.cosano.ordermanagement.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class OrderRepositoryIT {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.mongodb.database", () -> "orderdb-test");
    }

    @Test
    void repositoryPersistsAndRetrievesOrderUsingMongoContainer() {
        orderRepository.deleteAll();

        Order order = Order.builder()
                .productIds(List.of("product-1", "product-2"))
                .totalAmount(74.99)
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);
        Optional<Order> retrievedOrder = orderRepository.findById(savedOrder.getId());

        assertTrue(retrievedOrder.isPresent());
        assertEquals(List.of("product-1", "product-2"), retrievedOrder.get().getProductIds());
        assertEquals(74.99, retrievedOrder.get().getTotalAmount());
        assertEquals(OrderStatus.CREATED, retrievedOrder.get().getStatus());
    }
}
