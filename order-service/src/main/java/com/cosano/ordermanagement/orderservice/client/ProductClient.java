package com.cosano.ordermanagement.orderservice.client;

import com.cosano.ordermanagement.orderservice.dto.ProductResponse;
import com.cosano.ordermanagement.orderservice.exception.ProductServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductClient {

    private final WebClient webClient;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public ProductResponse getProductById(String productId) {
        try {
            return webClient.get()
                    .uri(productServiceUrl + "/products/{id}", productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new ProductServiceException("Product not found: " + productId)))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new ProductServiceException("product-service returned server error")))
                    .bodyToMono(ProductResponse.class)
                    .block();
        } catch (ProductServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ProductServiceException("Error calling product-service");
        }
    }
}