package com.cosano.ordermanagement.productservice.service;

import com.cosano.ordermanagement.productservice.dto.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void getAllProductsReturnsMappedResponses() {
        Product firstProduct = Product.builder()
                .id("product-1")
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(89.99)
                .category("electronics")
                .stock(25)
                .active(true)
                .build();

        Product secondProduct = Product.builder()
                .id("product-2")
                .name("Desk Lamp")
                .description("LED desk lamp")
                .price(49.99)
                .category("home")
                .stock(12)
                .active(false)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(firstProduct, secondProduct));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("product-1", responses.get(0).getId());
        assertEquals("Keyboard", responses.get(0).getName());
        assertTrue(responses.get(0).getActive());
        assertEquals("product-2", responses.get(1).getId());
        assertEquals("Desk Lamp", responses.get(1).getName());
        assertFalse(responses.get(1).getActive());
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
    void updateProductThrowsWhenProductDoesNotExist() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("New Name");
        request.setDescription("Updated description");
        request.setPrice(19.99);
        request.setCategory("new-category");
        request.setStock(15);
        request.setActive(true);

        when(productRepository.findById("missing-product")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct("missing-product", request)
        );

        assertEquals("Product not found with id: missing-product", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
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

    @Test
    void deleteProductThrowsWhenProductDoesNotExist() {
        when(productRepository.findById("missing-product")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct("missing-product")
        );

        assertEquals("Product not found with id: missing-product", exception.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getProductsByCategoryReturnsMappedResponses() {
        Product product = Product.builder()
                .id("product-1")
                .name("Monitor")
                .description("4K monitor")
                .price(399.0)
                .category("electronics")
                .stock(8)
                .active(true)
                .build();

        when(productRepository.findByCategoryIgnoreCase("electronics")).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getProductsByCategory("electronics");

        assertEquals(1, responses.size());
        assertEquals("product-1", responses.get(0).getId());
        assertEquals("Monitor", responses.get(0).getName());
        verify(productRepository).findByCategoryIgnoreCase("electronics");
    }

    @Test
    void getActiveProductsReturnsMappedResponses() {
        Product activeProduct = Product.builder()
                .id("product-1")
                .name("Monitor")
                .description("4K monitor")
                .price(399.0)
                .category("electronics")
                .stock(8)
                .active(true)
                .build();

        when(productRepository.findByActiveTrue()).thenReturn(List.of(activeProduct));

        List<ProductResponse> responses = productService.getActiveProducts();

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getActive());
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void searchProductsUsesCategoryAndActiveWhenBothFiltersArePresent() {
        Product product = Product.builder()
                .id("product-1")
                .name("Monitor")
                .description("4K monitor")
                .price(399.0)
                .category("electronics")
                .stock(8)
                .active(true)
                .build();

        when(productRepository.findByCategoryIgnoreCaseAndActive("electronics", true))
                .thenReturn(List.of(product));

        List<ProductResponse> responses = productService.searchProducts("electronics", true);

        assertEquals(1, responses.size());
        assertEquals("product-1", responses.get(0).getId());
        verify(productRepository).findByCategoryIgnoreCaseAndActive("electronics", true);
    }

    @Test
    void searchProductsUsesCategoryWhenOnlyCategoryIsPresent() {
        when(productRepository.findByCategoryIgnoreCase("electronics")).thenReturn(List.of());

        List<ProductResponse> responses = productService.searchProducts("electronics", null);

        assertTrue(responses.isEmpty());
        verify(productRepository).findByCategoryIgnoreCase("electronics");
    }

    @Test
    void searchProductsUsesActiveWhenOnlyActiveIsPresent() {
        when(productRepository.findByActive(false)).thenReturn(List.of());

        List<ProductResponse> responses = productService.searchProducts(null, false);

        assertTrue(responses.isEmpty());
        verify(productRepository).findByActive(false);
    }

    @Test
    void searchProductsUsesFindAllWhenNoFiltersArePresent() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> responses = productService.searchProducts(null, null);

        assertTrue(responses.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void getPagedProductsBuildsPagedResponseWithAscendingSort() {
        Product product = Product.builder()
                .id("product-1")
                .name("Desk Lamp")
                .description("LED desk lamp")
                .price(49.99)
                .category("home")
                .stock(12)
                .active(true)
                .build();

        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> productPage = new PageImpl<>(List.of(product), expectedPageable, 1);

        when(productRepository.findAll(eq(expectedPageable))).thenReturn(productPage);

        PagedResponse<ProductResponse> response = productService.getPagedProducts(0, 10, "name", "asc");

        assertEquals(1, response.getContent().size());
        assertEquals("product-1", response.getContent().get(0).getId());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isFirst());
        assertTrue(response.isLast());
    }

    @Test
    void getPagedProductsBuildsPagedResponseWithDescendingSort() {
        Pageable expectedPageable = PageRequest.of(1, 5, Sort.by("price").descending());
        Page<Product> productPage = new PageImpl<>(List.of(), expectedPageable, 0);

        when(productRepository.findAll(eq(expectedPageable))).thenReturn(productPage);

        PagedResponse<ProductResponse> response = productService.getPagedProducts(1, 5, "price", "desc");

        assertTrue(response.getContent().isEmpty());
        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        verify(productRepository).findAll(eq(expectedPageable));
    }
}
