package com.ecommerce.order.service.impl;

import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderItemResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.ProductResponseRecord;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.Observation;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final ProductClient productClient;

    private final OrderRepository orderRepository;

    private final ObservationRegistry observationRegistry;

    public OrderServiceImpl(ProductClient productClient, OrderRepository orderRepository, ObservationRegistry observationRegistry) {
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        log.info("Creating order for customerId={}", request.customerId());

        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        if (request.items() != null) {
            for (OrderItemRequest itemReq : request.items()) {
                OrderItem item = new OrderItem();
                item.setProductId(itemReq.productId());
                item.setQuantity(itemReq.quantity());
                log.info("Fetching product productId={}", itemReq.productId());
                //ProductResponseRecord product = productClient.getProduct(itemReq.productId());
                ProductResponseRecord product =
                Observation.createNotStarted(
                        "product.lookup",
                        observationRegistry
                ).observe(() ->
                        productClient.getProduct(itemReq.productId())
                );
                log.info("Product retrieved productId={}, price={}",product.id(),product.price());

                BigDecimal unitPrice = product.price();
                item.setUnitPrice(unitPrice);
                item.setOrder(order);
                items.add(item);

                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("Order created orderId={}, totalAmount={}",savedOrder.getId(),savedOrder.getTotalAmount());

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() != null ?
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList() : List.of();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
