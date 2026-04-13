package com.cosano.ordermanagement.orderservice.dto;

import lombok.Data;

@Data
public class ProductResponse {

    private String id;
    private String name;
    private Double price;
    private Boolean active;
}