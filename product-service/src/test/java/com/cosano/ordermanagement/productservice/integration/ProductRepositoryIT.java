package com.cosano.ordermanagement.productservice.integration;

import com.cosano.ordermanagement.productservice.entity.Product;
import com.cosano.ordermanagement.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class ProductRepositoryIT {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.mongodb.database", () -> "productdb-test");
    }

    @Test
    void repositoryPersistsAndRetrievesProductUsingMongoContainer() {
        productRepository.deleteAll();

        Product product = Product.builder()
                .name("Laptop Stand")
                .description("Aluminum stand")
                .price(39.99)
                .category("office")
                .stock(12)
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);
        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());

        assertTrue(retrievedProduct.isPresent());
        assertEquals("Laptop Stand", retrievedProduct.get().getName());
        assertEquals(39.99, retrievedProduct.get().getPrice());
        assertTrue(retrievedProduct.get().getActive());
    }
}
