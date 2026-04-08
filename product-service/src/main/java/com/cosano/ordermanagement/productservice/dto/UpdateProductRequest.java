package com.cosano.ordermanagement.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private Double price;

    @NotBlank
    private String category;

    @NotNull
    @Positive
    private Integer stock;

    @NotNull
    private Boolean active;
}