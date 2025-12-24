package com.stocksim.matching.engine;

import com.stocksim.matching.model.OrderEvent;
import com.stocksim.matching.model.TradeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderBookServiceTest {

    @Mock
    private KafkaTemplate<String, TradeEvent> kafkaTemplate;

    private OrderBookService orderBookService;

    @BeforeEach
    public void setUp() {
        orderBookService = new OrderBookService(kafkaTemplate);
    }

    @Test
    public void shouldMatchFullOrder() {
        // GIVEN: A Sell order already exists in the book
        OrderEvent sellOrder = createOrder("SELL", 10, 100.0);
        orderBookService.processOrder(sellOrder);

        // WHEN: A matching Buy order arrives
        OrderEvent buyOrder = createOrder("BUY", 10, 100.0);
        orderBookService.processOrder(buyOrder);

        // THEN: A trade event should be published
        ArgumentCaptor<TradeEvent> tradeCaptor = ArgumentCaptor.forClass(TradeEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("trade.executed"), anyString(), tradeCaptor.capture());

        TradeEvent trade = tradeCaptor.getValue();
        assertEquals(10, trade.getQuantity());
        assertEquals(BigDecimal.valueOf(100.0), trade.getPrice());
    }

    @Test
    public void shouldMatchPartialOrder() {
        // GIVEN: A Sell order for 5 items
        OrderEvent sellOrder = createOrder("SELL", 5, 100.0);
        orderBookService.processOrder(sellOrder);

        // WHEN: A Buy order for 10 items arrives
        OrderEvent buyOrder = createOrder("BUY", 10, 100.0);
        orderBookService.processOrder(buyOrder);

        // THEN: Trade executed for 5 items (only what was available)
        ArgumentCaptor<TradeEvent> tradeCaptor = ArgumentCaptor.forClass(TradeEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("trade.executed"), anyString(), tradeCaptor.capture());

        TradeEvent trade = tradeCaptor.getValue();
        assertEquals(5, trade.getQuantity());
    }

    @Test
    public void shouldNotMatchIfPriceMismatch() {
        // GIVEN: Sell order is at $105
        OrderEvent sellOrder = createOrder("SELL", 10, 105.0);
        orderBookService.processOrder(sellOrder);

        // WHEN: Buy order is at $100
        OrderEvent buyOrder = createOrder("BUY", 10, 100.0);
        orderBookService.processOrder(buyOrder);

        // THEN: No trade should happen
        verify(kafkaTemplate, times(0)).send(anyString(), anyString(), any(TradeEvent.class));
    }

    private OrderEvent createOrder(String side, int qty, double price) {
        return new OrderEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "AAPL",
                side,
                qty,
                BigDecimal.valueOf(price)
        );
    }
}
