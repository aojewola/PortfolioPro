package com.task.PortfolioPro.api.v1;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.task.portfoliopro.api.v1.StockController;
import com.task.portfoliopro.dto.PortfolioUpdateDTO;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.dto.StockPriceDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.services.StockService;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = StockController.class)
@WithMockUser("mock_user")
public class StockControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private ConnectionFactory connectionFactory;

    private final String url = "/api/v1/portfoliopro";

    @Test
    void testGetAllStocks() {
        List<Stock> stocks = List.of(new Stock("AAPL", "Apple Inc", url, 10, 150.0, false, 1500));
        when(stockService.getAllStocks()).thenReturn(Flux.fromIterable(stocks));

        webTestClient.get()
                .uri("/api/v1/portfoliopro/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Stock.class)
                .hasSize(1)
                .contains(stocks.get(0));
    }

    @Test
    void testAddStock() {
        StockDTO stockDto = new StockDTO("AAPL", "Apple Inc", 10, 100);
        Stock stock = new Stock("id-3459k-ky76", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        when(stockService.addStock(stockDto)).thenReturn(Mono.just(stock));

        webTestClient.post()
                .uri("/api/v1/portfoliopro/stocks")
                .bodyValue(stockDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .isEqualTo(stock);
    }

    @Test
    void testRemoveStock() {
        Stock stock = new Stock("id-3459k-ky76-uyt", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        when(stockService.removeStock("1")).thenReturn(Mono.just(stock));

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .isEqualTo(stock);
    }

    @Test
    void testDeleteStock() {
        when(stockService.deleteStock("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/1/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testGetTotalPortfolioValue() {
        PortfolioUpdateDTO portfolioUpdateDTO = new PortfolioUpdateDTO(1000.0, 2000);
        when(stockService.totalPortfolioValue()).thenReturn(Mono.just(portfolioUpdateDTO));

        webTestClient.get()
                .uri("/api/v1/portfoliopro/total-value")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PortfolioUpdateDTO.class)
                .isEqualTo(portfolioUpdateDTO);
    }

    @Test
    void testGetRealTimeStockPrice() {
        StockPriceDTO stockPriceDTO = new StockPriceDTO(200, 150.0, 190, 100);
        when(stockService.getCurrentStockPrice("AAPL")).thenReturn(Mono.just(stockPriceDTO));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/portfoliopro/stock-price")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockPriceDTO.class)
                .isEqualTo(stockPriceDTO);
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
