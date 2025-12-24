package com.stocksim.matching.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeEvent {
    private String matchId;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private String symbol;
    private Integer quantity;
    private BigDecimal price;
    private Long timestamp;
}
