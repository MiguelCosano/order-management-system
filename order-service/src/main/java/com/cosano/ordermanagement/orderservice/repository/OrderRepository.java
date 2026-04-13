package com.cosano.ordermanagement.orderservice.repository;

import com.cosano.ordermanagement.orderservice.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}