package com.stocksim.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private String symbol;
    
    @Enumerated(EnumType.STRING)
    private OrderSide side; // BUY or SELL

    private Integer quantity;
    private BigDecimal price; // Limit price

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // CREATED, FILLED, REJECTED

    private LocalDateTime createdAt;
}
