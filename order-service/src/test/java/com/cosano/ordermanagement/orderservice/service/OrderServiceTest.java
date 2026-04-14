package com.cosano.ordermanagement.orderservice.service;

import com.cosano.ordermanagement.orderservice.client.ProductClient;
import com.cosano.ordermanagement.orderservice.dto.OrderResponse;
import com.cosano.ordermanagement.orderservice.dto.ProductResponse;
import com.cosano.ordermanagement.orderservice.entity.Order;
import com.cosano.ordermanagement.orderservice.entity.OrderStatus;
import com.cosano.ordermanagement.orderservice.exception.InvalidOrderStateException;
import com.cosano.ordermanagement.orderservice.exception.OrderNotFoundException;
import com.cosano.ordermanagement.orderservice.exception.ProductServiceException;
import com.cosano.ordermanagement.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderSucceedsWhenAllProductsAreValidAndActive() {
        ProductResponse firstProduct = product("product-1", 49.99, true);
        ProductResponse secondProduct = product("product-2", 25.00, true);

        when(productClient.getProductById("product-1")).thenReturn(firstProduct);
        when(productClient.getProductById("product-2")).thenReturn(secondProduct);
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("order-1");
            return order;
        });

        OrderResponse response = orderService.createOrder(List.of("product-1", "product-2"));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order persistedOrder = captor.getValue();
        assertEquals(List.of("product-1", "product-2"), persistedOrder.getProductIds());
        assertEquals(74.99, persistedOrder.getTotalAmount(), 0.0001);
        assertEquals(OrderStatus.CREATED, persistedOrder.getStatus());
        assertEquals("order-1", response.getId());
        assertEquals(74.99, response.getTotalAmount(), 0.0001);
        assertEquals(OrderStatus.CREATED, response.getStatus());
    }

    @Test
    void createOrderFailsWhenProductIsInactive() {
        when(productClient.getProductById("product-1")).thenReturn(product("product-1", 49.99, false));

        ProductServiceException exception = assertThrows(
                ProductServiceException.class,
                () -> orderService.createOrder(List.of("product-1"))
        );

        assertEquals("Product is inactive: product-1", exception.getMessage());
    }

    @Test
    void createOrderFailsWhenProductIsMissing() {
        when(productClient.getProductById("missing-product")).thenReturn(null);

        ProductServiceException exception = assertThrows(
                ProductServiceException.class,
                () -> orderService.createOrder(List.of("missing-product"))
        );

        assertEquals("Product not found: missing-product", exception.getMessage());
    }

    @Test
    void getOrderByIdReturnsOrderSuccessfully() {
        Order order = Order.builder()
                .id("order-1")
                .productIds(List.of("product-1"))
                .totalAmount(49.99)
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById("order-1");

        assertEquals("order-1", response.getId());
        assertEquals(List.of("product-1"), response.getProductIds());
        assertEquals(49.99, response.getTotalAmount());
        assertEquals(OrderStatus.CREATED, response.getStatus());
    }

    @Test
    void getOrderByIdThrowsWhenOrderDoesNotExist() {
        when(orderRepository.findById("missing-order")).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById("missing-order")
        );

        assertEquals("Order not found with id: missing-order", exception.getMessage());
    }

    @Test
    void cancelOrderUpdatesStatusSuccessfully() {
        Order existingOrder = Order.builder()
                .id("order-1")
                .productIds(List.of("product-1"))
                .totalAmount(49.99)
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponse response = orderService.cancelOrder("order-1");

        assertEquals(OrderStatus.CANCELLED, existingOrder.getStatus());
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
    }

    @Test
    void cancelOrderFailsWhenOrderIsAlreadyCancelled() {
        Order cancelledOrder = Order.builder()
                .id("order-1")
                .productIds(List.of("product-1"))
                .totalAmount(49.99)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(cancelledOrder));

        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.cancelOrder("order-1")
        );

        assertEquals("Order is already cancelled: order-1", exception.getMessage());
        assertTrue(cancelledOrder.getStatus() == OrderStatus.CANCELLED);
    }

    private ProductResponse product(String id, double price, boolean active) {
        ProductResponse response = new ProductResponse();
        response.setId(id);
        response.setPrice(price);
        response.setActive(active);
        return response;
    }
}
