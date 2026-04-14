package com.cosano.ordermanagement.orderservice.dto;

import com.cosano.ordermanagement.orderservice.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponse {

    private String id;

    private List<String> productIds;

    private Double totalAmount;

    private OrderStatus status;
}