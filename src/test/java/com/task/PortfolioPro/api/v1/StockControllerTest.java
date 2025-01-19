package com.task.PortfolioPro.api.v1;

import static org.mockito.ArgumentMatchers.any;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = StockController.class)
@WithMockUser("mock_user")
public class StockControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private StockService stockService;

    private final String url = "/api/v1/portfoliopro";

    @Test
    void testGetAllStocks() {
        List<Stock> stocks = List.of(new Stock("id-32e5r", "Apple Inc", "AAPL", 10, 150.0, false, 1500));
        when(stockService.getAllStocks()).thenReturn(Flux.fromIterable(stocks));

        webTestClient.get()
                .uri(url + "/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Stock.class)
                .hasSize(1).value(res -> res.get(0).equals(stocks.get(0)));
    }

    @Test
    void testAddStock() {
        StockDTO stockDto = new StockDTO("AAPL", "Apple Inc", 10, 100);
        Stock stock = new Stock("id-3459k-ky76", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        when(stockService.addStock(any(StockDTO.class))).thenReturn(Mono.just(stock));

        webTestClient.post()
                .uri(url + "/stocks")
                .bodyValue(stockDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .value(res -> res.getId().equals(stock.getId()))
                .value(res -> res.equals(stock));
    }

    @Test
    void testRemoveStock() {
        Stock stock = new Stock("id-3459k-ky76-uyt", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        when(stockService.removeStock("id-3459k-ky76-uyt")).thenReturn(Mono.just(stock));

        webTestClient.delete()
                .uri("/api/v1/portfoliopro/stocks/id-3459k-ky76-uyt")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Stock.class)
                .value(result -> result.isDeleted());
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
                .uri(url + "/total-value")
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
                .uri(uriBuilder -> uriBuilder.path(url + "/stock-price")
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
                .uri(uriBuilder -> uriBuilder.path(url + "/stocks/time-series")
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
                .uri(uriBuilder -> uriBuilder.path(url + "/company-info")
                        .queryParam("ticker", "AAPL")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(companyInfo);
    }

}
