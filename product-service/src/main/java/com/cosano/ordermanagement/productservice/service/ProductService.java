package com.cosano.ordermanagement.productservice.service;

import com.cosano.ordermanagement.productservice.dto.PagedResponse;
import com.cosano.ordermanagement.productservice.dto.ProductRequest;
import com.cosano.ordermanagement.productservice.dto.ProductResponse;
import com.cosano.ordermanagement.productservice.dto.UpdateProductRequest;
import com.cosano.ordermanagement.productservice.entity.Product;
import com.cosano.ordermanagement.productservice.exception.ProductNotFoundException;
import com.cosano.ordermanagement.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(String id) {
        Product product = findProductOrThrow(id);
        return mapToResponse(product);
    }

    @CachePut(value = "products", key = "#id")
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        Product existingProduct = findProductOrThrow(id);

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setStock(request.getStock());
        existingProduct.setActive(request.getActive());

        Product updated = productRepository.save(existingProduct);
        return mapToResponse(updated);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(String id) {
        Product existingProduct = findProductOrThrow(id);
        productRepository.delete(existingProduct);
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String category, Boolean active) {
        List<Product> products;

        if (category != null && active != null) {
            products = productRepository.findByCategoryIgnoreCaseAndActive(category, active);
        } else if (category != null) {
            products = productRepository.findByCategoryIgnoreCase(category);
        } else if (active != null) {
            products = productRepository.findByActive(active);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Product findProductOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stock(product.getStock())
                .active(product.getActive())
                .build();
    }

    public PagedResponse<ProductResponse> getPagedProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }
}
