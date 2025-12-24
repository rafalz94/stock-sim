package com.stocksim.order.controller;

import com.stocksim.order.model.Order;
import com.stocksim.order.model.OrderSide;
import com.stocksim.order.model.OrderStatus;
import com.stocksim.order.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request) {
        // 1. Save to DB
        Order order = Order.builder()
                .userId(UUID.randomUUID()) // Mock user ID for now
                .symbol(request.getSymbol())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);
        log.info("Order saved: {}", order.getId());

        // 2. Publish to Kafka
        OrderEvent event = new OrderEvent(
                order.getId(),
                order.getUserId(),
                order.getSymbol(),
                order.getSide(),
                order.getQuantity(),
                order.getPrice()
        );
        kafkaTemplate.send("orders.created", order.getSymbol(), event);

        return order;
    }

    @Data
    public static class OrderRequest {
        private String symbol;
        private OrderSide side;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @AllArgsConstructor
    public static class OrderEvent {
        private UUID orderId;
        private UUID userId;
        private String symbol;
        private OrderSide side;
        private Integer quantity;
        private BigDecimal price;
    }
}
