package com.cosano.ordermanagement.productservice.service;

import com.cosano.ordermanagement.productservice.dto.ProductRequest;
import com.cosano.ordermanagement.productservice.dto.ProductResponse;
import com.cosano.ordermanagement.productservice.dto.UpdateProductRequest;
import com.cosano.ordermanagement.productservice.entity.Product;
import com.cosano.ordermanagement.productservice.exception.ProductNotFoundException;
import com.cosano.ordermanagement.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProductCreatesAnActiveProductSuccessfully() {
        ProductRequest request = new ProductRequest();
        request.setName("Wireless Mouse");
        request.setDescription("Bluetooth mouse");
        request.setPrice(29.99);
        request.setCategory("electronics");
        request.setStock(100);

        Product savedProduct = Product.builder()
                .id("product-1")
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product persistedProduct = captor.getValue();
        assertEquals("Wireless Mouse", persistedProduct.getName());
        assertTrue(persistedProduct.getActive());
        assertEquals("product-1", response.getId());
        assertEquals(29.99, response.getPrice());
        assertTrue(response.getActive());
    }

    @Test
    void getProductByIdReturnsProductSuccessfully() {
        Product product = Product.builder()
                .id("product-1")
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(89.99)
                .category("electronics")
                .stock(25)
                .active(true)
                .build();

        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById("product-1");

        assertEquals("product-1", response.getId());
        assertEquals("Keyboard", response.getName());
        assertEquals(89.99, response.getPrice());
        assertTrue(response.getActive());
    }

    @Test
    void getProductByIdThrowsWhenProductDoesNotExist() {
        when(productRepository.findById("missing-product")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById("missing-product")
        );

        assertEquals("Product not found with id: missing-product", exception.getMessage());
    }

    @Test
    void updateProductUpdatesExistingProductSuccessfully() {
        Product existingProduct = Product.builder()
                .id("product-1")
                .name("Old Name")
                .description("Old description")
                .price(10.0)
                .category("old-category")
                .stock(5)
                .active(true)
                .build();

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("New Name");
        request.setDescription("Updated description");
        request.setPrice(19.99);
        request.setCategory("new-category");
        request.setStock(15);
        request.setActive(false);

        Product updatedProduct = Product.builder()
                .id("product-1")
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .active(request.getActive())
                .build();

        when(productRepository.findById("product-1")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct("product-1", request);

        assertEquals("New Name", existingProduct.getName());
        assertEquals("Updated description", existingProduct.getDescription());
        assertEquals(19.99, existingProduct.getPrice());
        assertEquals("new-category", existingProduct.getCategory());
        assertEquals(15, existingProduct.getStock());
        assertFalse(existingProduct.getActive());
        assertEquals("product-1", response.getId());
        assertFalse(response.getActive());
    }

    @Test
    void deleteProductDeletesExistingProductSuccessfully() {
        Product existingProduct = Product.builder()
                .id("product-1")
                .name("Monitor")
                .description("4K monitor")
                .price(399.0)
                .category("electronics")
                .stock(8)
                .active(true)
                .build();

        when(productRepository.findById("product-1")).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct("product-1");

        verify(productRepository).delete(existingProduct);
        verify(productRepository, never()).save(any(Product.class));
        assertNotNull(existingProduct);
    }
}
