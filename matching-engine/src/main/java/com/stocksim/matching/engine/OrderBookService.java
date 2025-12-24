package com.stocksim.matching.engine;

import com.stocksim.matching.model.OrderEvent;
import com.stocksim.matching.model.TradeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OrderBookService {

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;
    
    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    public OrderBookService(KafkaTemplate<String, TradeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processOrder(OrderEvent order) {
        orderBooks.putIfAbsent(order.getSymbol(), new OrderBook(order.getSymbol()));
        OrderBook book = orderBooks.get(order.getSymbol());
        
        List<TradeEvent> trades = book.match(order);
        
        trades.forEach(trade -> {
            log.info("TRADE EXECUTED: {} @ {}", trade.getQuantity(), trade.getPrice());
            kafkaTemplate.send("trade.executed", trade.getSymbol(), trade);
        });
    }

    private static class OrderBook {
        private final String symbol;
        
        private final PriorityQueue<OrderEvent> bids = new PriorityQueue<>(
            (o1, o2) -> o2.getPrice().compareTo(o1.getPrice())
        );
        
        private final PriorityQueue<OrderEvent> asks = new PriorityQueue<>(
            (o1, o2) -> o1.getPrice().compareTo(o2.getPrice())
        );

        public OrderBook(String symbol) {
            this.symbol = symbol;
        }

        public synchronized List<TradeEvent> match(OrderEvent incomingOrder) {
            List<TradeEvent> trades = new ArrayList<>();
            
            if ("BUY".equals(incomingOrder.getSide())) {
                processBuy(incomingOrder, trades);
            } else {
                processSell(incomingOrder, trades);
            }
            
            return trades;
        }

        private void processBuy(OrderEvent buyOrder, List<TradeEvent> trades) {
            while (buyOrder.getQuantity() > 0 && !asks.isEmpty()) {
                OrderEvent bestAsk = asks.peek();
                
                if (bestAsk.getPrice().compareTo(buyOrder.getPrice()) > 0) {
                    break;
                }
                
                int quantity = Math.min(buyOrder.getQuantity(), bestAsk.getQuantity());
                
                trades.add(new TradeEvent(
                        UUID.randomUUID().toString(),
                        buyOrder.getOrderId(),
                        bestAsk.getOrderId(),
                        symbol,
                        quantity,
                        bestAsk.getPrice(),
                        System.currentTimeMillis()
                ));
                
                buyOrder.setQuantity(buyOrder.getQuantity() - quantity);
                bestAsk.setQuantity(bestAsk.getQuantity() - quantity);
                
                if (bestAsk.getQuantity() == 0) {
                    asks.poll();
                }
            }
            
            if (buyOrder.getQuantity() > 0) {
                bids.add(buyOrder);
            }
        }

        private void processSell(OrderEvent sellOrder, List<TradeEvent> trades) {
            while (sellOrder.getQuantity() > 0 && !bids.isEmpty()) {
                OrderEvent bestBid = bids.peek();
                
                if (bestBid.getPrice().compareTo(sellOrder.getPrice()) < 0) {
                    break;
                }
                
                int quantity = Math.min(sellOrder.getQuantity(), bestBid.getQuantity());
                
                trades.add(new TradeEvent(
                        UUID.randomUUID().toString(),
                        bestBid.getOrderId(),
                        sellOrder.getOrderId(),
                        symbol,
                        quantity,
                        bestBid.getPrice(),
                        System.currentTimeMillis()
                ));
                
                sellOrder.setQuantity(sellOrder.getQuantity() - quantity);
                bestBid.setQuantity(bestBid.getQuantity() - quantity);
                
                if (bestBid.getQuantity() == 0) {
                    bids.poll();
                }
            }
            
            if (sellOrder.getQuantity() > 0) {
                asks.add(sellOrder);
            }
        }
    }
}
