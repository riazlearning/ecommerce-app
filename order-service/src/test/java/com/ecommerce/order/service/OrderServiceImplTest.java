package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID productId1;
    private UUID productId2;

    @BeforeEach
    void setUp() {
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    @DisplayName("createOrder should calculate total amount and set item unit prices correctly")
    void createOrder_Success() {
        // Given
        OrderItemRequest item1 = new OrderItemRequest(productId1, 2, new BigDecimal("25.00"));
        OrderItemRequest item2 = new OrderItemRequest(productId2, 1, new BigDecimal("50.00"));
        OrderRequest request = new OrderRequest(100L, List.of(item1, item2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            savedOrder.setCreatedAt(LocalDateTime.now());
            savedOrder.setUpdatedAt(LocalDateTime.now());
            return savedOrder;
        });

        // When
        OrderResponse response = orderService.createOrder(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.customerId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).productId()).isEqualTo(productId1);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.items().get(0).unitPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(response.items().get(1).productId()).isEqualTo(productId2);
        assertThat(response.items().get(1).quantity()).isEqualTo(1);
        assertThat(response.items().get(1).unitPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("getOrderById should return OrderResponse when order exists")
    void getOrderById_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(100L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(new BigDecimal("75.00"));
        
        OrderItem item = new OrderItem(10L, productId1, 3, new BigDecimal("25.00"), order);
        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        OrderResponse response = orderService.getOrderById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productId()).isEqualTo(productId1);
    }

    @Test
    @DisplayName("getOrderById should throw exception when order does not exist")
    void getOrderById_NotFound() {
        // Given
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found with id: 99");
    }

    @Test
    @DisplayName("getOrdersByCustomer should return list of orders for given customerId")
    void getOrdersByCustomer_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(100L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("50.00"));

        when(orderRepository.findByCustomerId(100L)).thenReturn(List.of(order));

        // When
        List<OrderResponse> responses = orderService.getOrdersByCustomer(100L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).customerId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("updateOrderStatus should update and return order with new status")
    void updateOrderStatus_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(100L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(new BigDecimal("50.00"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // When
        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Then
        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(order);
    }
}
