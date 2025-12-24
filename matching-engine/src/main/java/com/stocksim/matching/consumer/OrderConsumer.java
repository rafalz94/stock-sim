package com.stocksim.matching.consumer;

import com.stocksim.matching.engine.OrderBookService;
import com.stocksim.matching.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderBookService orderBookService;

    @KafkaListener(topics = "orders.created", groupId = "matching-engine-group")
    public void consumeOrder(OrderEvent orderEvent) {
        log.info("Received Order: {} {} {} @ {}", 
                orderEvent.getSide(), orderEvent.getQuantity(), orderEvent.getSymbol(), orderEvent.getPrice());
        
        orderBookService.processOrder(orderEvent);
    }
}
