package com.stocksim.market.service;

import com.stocksim.market.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceStreamer {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Listens to internal Kafka topic and broadcasts to external WebSocket clients.
     */
    @KafkaListener(topics = "market.prices", groupId = "market-ws-group")
    public void consumePrice(StockPrice stockPrice) {
        // Broadcast to all subscribers of /topic/prices
        messagingTemplate.convertAndSend("/topic/prices", stockPrice);
    }
}
