package com.task.PortfolioPro.api.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.task.portfoliopro.dto.PortfolioUpdateDTO;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.dto.StockPriceDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.services.StockService;

import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@WithMockUser("test")
public class StockControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StockService stockService;

    @Test
    void testGetAllStocks() {
        Stock stock1 = new Stock("id-test", "Apple Inc", "APPL", 10, 150.0, false, 1500);
        Stock stock2 = new Stock("id-test-32", "Alphabet Inc", "GOOGL", 5, 2800.0, false, 1400);

        stockService.addStock(new StockDTO( "Apple Inc", "AAPL", 10, 10)).block();
        stockService.addStock(new StockDTO( "Alphabet Inc", "GOOGL", 5, 10)).block();

        webTestClient.get()
                .uri("/api/v1/portfoliopro/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Stock.class)
                .hasSize(2)
                .contains(stock1, stock2);
    }

    @Test
    void testAddStock() {
        StockDTO stockDto = new StockDTO("Microsoft Corp", "MSFT", 15, 10);

        webTestClient.post()
                .uri("/api/v1/portfoliopro/stocks")
                .bodyValue(stockDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .consumeWith(response -> {
                    Stock stock = response.getResponseBody();
                    assertNotNull(stock);
                    assertEquals("MSFT", stock.getTicker());
                    assertEquals("Microsoft Corp", stock.getStockName());
                    assertEquals(15, stock.getShares());
                });
    }

    @Test
    void testRemoveStock() {
        Stock stock = stockService.addStock(new StockDTO("Tesla Inc", "TSLA", 8, 10)).block();

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/" + stock.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .consumeWith(response -> {
                    Stock removedStock = response.getResponseBody();
                    assertNotNull(removedStock);
                    assertEquals("TSLA", removedStock.getTicker());
                });
    }

    @Test
    void testDeleteStock() {
        Stock stock = stockService.addStock(new StockDTO("Netflix Inc", "NFLX", 12, 10)).block();

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/" + stock.getId() + "/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void testGetTotalPortfolioValue() {
        stockService.addStock(new StockDTO("Apple Inc", "AAPL", 10, 10)).block();
        stockService.addStock(new StockDTO( "Alphabet Inc", "GOOGL",5, 10)).block();

        webTestClient.get()
                .uri("/api/v1/portfoliopro/total-value")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PortfolioUpdateDTO.class)
                .consumeWith(response -> {
                    PortfolioUpdateDTO portfolioValue = response.getResponseBody();
                    assertNotNull(portfolioValue);
                    assertTrue(portfolioValue.getCurrentPortfolioValue() > 0);
                });
    }

    @Test
    void testGetRealTimeStockPrice() {
        StockPriceDTO stockPriceDTO = new StockPriceDTO(112, 122, 134, 99.0);
        when(stockService.getCurrentStockPrice("AAPL")).thenReturn(Mono.just(stockPriceDTO));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/stock-price")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockPriceDTO.class)
                .consumeWith(response -> {
                    StockPriceDTO stockPrice = response.getResponseBody();
                    assertNotNull(stockPrice);
                    assertEquals(112, stockPrice.getOpenPrice());
                    assertEquals(99.0, stockPrice.getLowPrice());
                });
    }

    @Test
    void testGetTimeSeriesStockPrice() {
        String timeSeriesData = "{\"data\": [100, 101, 102]}";
        when(stockService.stockRealTimeSeries("AAPL")).thenReturn(Mono.just(timeSeriesData));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/stocks/time-series")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(timeSeriesData);
    }

    @Test
    void testGetCompanyInfo() {
        String companyInfo = "{\"name\": \"Apple Inc\", \"ticker\": \"AAPL\"}";
        when(stockService.getCompanyInfo("AAPL")).thenReturn(Mono.just(companyInfo));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/company-info")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(companyInfo);
    }
}
