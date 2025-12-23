package com.stocksim.market.service;

import com.stocksim.market.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceGeneratorService {

    private final KafkaTemplate<String, StockPrice> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${finnhub.token}")
    private String apiToken;

    @Value("${finnhub.api-url}")
    private String apiUrl;

    @Value("${finnhub.symbols}")
    private String[] symbols;

    // Current prices (Updated from API or Simulation)
    private final Map<String, Double> stockPrices = new ConcurrentHashMap<>();

    /**
     * Fetches REAL prices from Finnhub every 10 seconds.
     * This ensures our base price is always accurate.
     */
    @Scheduled(fixedRate = 10000)
    public void fetchRealPrices() {
        for (String symbol : symbols) {
            try {
                String url = apiUrl + symbol + "&token=" + apiToken;
                // Finnhub response: {"c": 150.5, "d": ...} where 'c' is current price
                Map response = restTemplate.getForObject(url, Map.class);
                
                if (response != null && response.get("c") != null) {
                    Object priceObj = response.get("c");
                    Double price = Double.valueOf(priceObj.toString());
                    stockPrices.put(symbol, price);
                    log.info("Fetched REAL price for {}: {}", symbol, price);
                }
            } catch (Exception e) {
                log.error("Failed to fetch price for {}", symbol, e);
            }
        }
    }

    /**
     * Generates micro-movements every 1 second based on the last known real price.
     * This keeps the WebSocket stream alive and exciting.
     */
    @Scheduled(fixedRate = 1000)
    public void streamPrices() {
        stockPrices.forEach((symbol, currentPrice) -> {
            // Fluctuate price by -0.2% to +0.2% (Micro-movements)
            double changePercent = (random.nextDouble() - 0.5) * 0.004;
            double newPrice = currentPrice * (1 + changePercent);
            
            // Round to 2 decimals
            newPrice = BigDecimal.valueOf(newPrice)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            
            // Update map so next simulation uses this new base
            stockPrices.put(symbol, newPrice);

            StockPrice priceUpdate = new StockPrice(symbol, newPrice, System.currentTimeMillis());
            kafkaTemplate.send("market.prices", symbol, priceUpdate);
        });
    }
}
