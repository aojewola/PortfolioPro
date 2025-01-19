package com.task.PortfolioPro.api.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.task.portfoliopro.apiClients.AlphaVantageApiClient;
import com.task.portfoliopro.apiClients.FinnuhApiClient;
import com.task.portfoliopro.dto.PortfolioUpdateDTO;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.dto.StockPriceDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.repository.StockRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@WithMockUser("test")
public class StockControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private AlphaVantageApiClient alpha;

    @MockBean
    private FinnuhApiClient finnuhApiClient;

    @Test
    public void testExample() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
        SecurityContextHolder.setContext(context);
    }


    @Test
    void testGetAllStocks() {
        Stock stock1 = new Stock("id-test", "Apple Inc", "APPL", 10, 150.0, false, 1500);
        Stock stock2 = new Stock("id-test-32", "Alphabet Inc", "GOOGL", 5, 2800.0, false, 1400);

        when(stockRepository.findAllByIsDeletedFalse()).thenReturn(Flux.just(stock1, stock2));

        List<Stock> stocks = webTestClient.get()
                .uri("/api/v1/portfoliopro/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Stock.class)
                .hasSize(2)
               .returnResult().getResponseBody();

               assertNotNull(stocks);
               assertEquals(stock1, stocks.get(0));
               assertEquals(stock2, stocks.get(1));
    }

    @Test
    void testAddStock() {
        String timeSeriesData = "{\"Time Series (1min)\": {\n" + //
                        "        \"2025-01-19 20:00:00\": {\n" + //
                        "            \"1. open\": \"233.6800\",\n" + //
                        "            \"2. high\": \"233.7000\",\n" + //
                        "            \"3. low\": \"233.6500\",\n" + //
                        "            \"4. close\": \"233.6800\",\n" + //
                        "            \"5. volume\": \"5432\"\n" + //
                        "        }}}";

        StockDTO stockDto = new StockDTO("JPMORGAN", "JPM", 15, 10);
        Stock stockInput = new Stock("id-test", "JPMORGAN", "JPM", 15, 150.0, false, 1500);

        when(alpha.getStockPrice(anyString())).thenReturn(Mono.just(timeSeriesData));
        when(stockRepository.save(any(Stock.class))).thenReturn(Mono.just(stockInput));    

        webTestClient.post()
                .uri("/api/v1/portfoliopro/stocks")
                .bodyValue(stockDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .consumeWith(response -> {
                    Stock stock = response.getResponseBody();
                    assertNotNull(stock);
                    assertEquals("JPM", stock.getTicker());
                    assertEquals("JPMORGAN", stock.getStockName());
                    assertEquals(15, stock.getShares());
                });
    }

    @Test
    void testRemoveStock() {
        Stock stock = new Stock("id-test", "TESLA", "TSLA", 10, 150.0, false, 1500);
        when(stockRepository.findByIdAndIsDeletedFalse(anyString())).thenReturn(Mono.just(stock));

        stock.setDeleted(true);
        when(stockRepository.softDeleteById(anyString())).thenReturn(Mono.just("id-test"));

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
        when(stockRepository.deleteById(anyString())).thenReturn(null);

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/id-test-786/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void testGetTotalPortfolioValue() {
        String timeSeriesData = "{\"Time Series (1min)\": {\n" + //
                        "        \"2025-01-19 20:00:00\": {\n" + //
                        "            \"1. open\": \"233.6800\",\n" + //
                        "            \"2. high\": \"233.7000\",\n" + //
                        "            \"3. low\": \"233.6500\",\n" + //
                        "            \"4. close\": \"233.6800\",\n" + //
                        "            \"5. volume\": \"5432\"\n" + //
                        "        }}}";

        Stock stock = new Stock("id-test", "TESLA", "TSLA", 10, 150.0, false, 1500);
        when(alpha.getStockPrice(anyString())).thenReturn(Mono.just(timeSeriesData));
        when(stockRepository.findAllByIsDeletedFalse()).thenReturn(Flux.just(stock));


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
        String timeSeriesData = "{\"Time Series (1min)\": {\n" + //
                        "        \"2025-01-19 20:00:00\": {\n" + //
                        "            \"1. open\": \"233.6800\",\n" + //
                        "            \"2. high\": \"233.7000\",\n" + //
                        "            \"3. low\": \"233.6500\",\n" + //
                        "            \"4. close\": \"233.6800\",\n" + //
                        "            \"5. volume\": \"5432\"\n" + //
                        "        }}}";
       
        when(alpha.getStockPrice(anyString())).thenReturn(Mono.just(timeSeriesData));

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
                    assertEquals(233.68, stockPrice.getOpenPrice());
                    assertEquals(233.65, stockPrice.getLowPrice());
                });
    }

    @Test
    void testGetTimeSeriesStockPrice() {
        when(alpha.getStockPrice(anyString())).thenReturn(Mono.just("101"));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/stocks/time-series")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("101");
    }

    @Test
    void testGetCompanyInfo() {
        when(finnuhApiClient.getCompanyInfo(anyString())).thenReturn(Mono.just("JPMorgan"));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/company-info")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("JPMorgan");
    }
}
