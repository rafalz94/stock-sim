package com.stocksim.matching.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private UUID orderId;
    private UUID userId;
    private String symbol;
    private String side; // BUY or SELL
    private Integer quantity;
    private BigDecimal price;
}
