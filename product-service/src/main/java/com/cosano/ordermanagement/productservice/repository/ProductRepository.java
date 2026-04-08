package com.cosano.ordermanagement.productservice.repository;

import com.cosano.ordermanagement.productservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIgnoreCaseAndActive(String category, Boolean active);

    List<Product> findByActive(Boolean active);
}