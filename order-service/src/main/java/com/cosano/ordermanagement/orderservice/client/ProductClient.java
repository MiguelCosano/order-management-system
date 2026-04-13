package com.cosano.ordermanagement.orderservice.client;

import com.cosano.ordermanagement.orderservice.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductResponse getProductById(String productId) {
        try {
            String url = productServiceUrl + "/products/" + productId;
            return restTemplate.getForObject(url, ProductResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Product not found: " + productId);
        } catch (Exception ex) {
            throw new RuntimeException("Error calling product-service");
        }
    }
}