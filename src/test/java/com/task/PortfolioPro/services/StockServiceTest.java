package com.task.PortfolioPro.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.task.portfoliopro.apiClients.AlphaVantageApiClient;
import com.task.portfoliopro.apiClients.FinnuhApiClient;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.errors.HttpError;
import com.task.portfoliopro.repository.StockRepository;
import com.task.portfoliopro.services.StockService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringJUnitConfig
public class StockServiceTest {
    @Mock
    private StockRepository stockRepository;

    @Mock
    private AlphaVantageApiClient alphaVantageApiClient;

    @Mock
    private FinnuhApiClient finnuhApiClient;

    @InjectMocks
    private StockService stockService;

    @Test
    void testGetAllStocks() {
        Stock stock1 = new Stock("id-3459k-ky76", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        Stock stock2 = new Stock("id-test-32", "Alphabet Inc", "GOOGL", 5, 2800.0, false, 1400);
        when(stockRepository.findAllByIsDeletedFalse()).thenReturn(Flux.just(stock1, stock2));

        StepVerifier.create(stockService.getAllStocks())
                .expectNext(stock1)
                .expectNext(stock2)
                .verifyComplete();

        verify(stockRepository).findAllByIsDeletedFalse();
    }

    @Test
    void testAddStock() {
    
        StockDTO stockDTO = new StockDTO("AAPL", "Apple Inc", 10, 100);
        Stock savedStock = new Stock(stockDTO);
        savedStock.setPrice(150.0);

        when(alphaVantageApiClient.getStockPrice(stockDTO.getTicker()))
                .thenReturn(Mono.just("{ \"Time Series (1min)\": { \"close\": 150.0 } }"));
        when(stockRepository.save(any(Stock.class))).thenReturn(Mono.just(savedStock));

        StepVerifier.create(stockService.addStock(stockDTO))
                .expectNext(savedStock)
                .verifyComplete();

        verify(alphaVantageApiClient).getStockPrice("AAPL");
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void testRemoveStock() {
        String stockId = "1";
        Stock existingStock = new Stock("id-3459k-ky76-uyt", "Apple Inc", "AAPL", 10, 150.0, false, 1500);

        when(stockRepository.findByIdAndIsDeletedFalse(stockId))
                .thenReturn(Mono.just(existingStock));
        when(stockRepository.softDeleteById(stockId)).thenReturn(Mono.empty());

        StepVerifier.create(stockService.removeStock(stockId))
                .expectNext(existingStock)
                .verifyComplete();

        verify(stockRepository).findByIdAndIsDeletedFalse(stockId);
        verify(stockRepository).softDeleteById(stockId);
    }

    @Test
    void testRemoveStock_NotFound() {
        String stockId = "1";
        when(stockRepository.findByIdAndIsDeletedFalse(stockId)).thenReturn(Mono.empty());

        StepVerifier.create(stockService.removeStock(stockId))
                .expectErrorMatches(throwable -> throwable instanceof HttpError &&
                        throwable.getMessage().contains("No stock found with the id"))
                .verify();

        verify(stockRepository).findByIdAndIsDeletedFalse(stockId);
        verify(stockRepository, never()).softDeleteById(anyString());
    }

    @Test
    void testDeleteStock() {
        String stockId = "1";
        when(stockRepository.deleteById(stockId)).thenReturn(Mono.empty());

        StepVerifier.create(stockService.deleteStock(stockId))
                .verifyComplete();

        verify(stockRepository).deleteById(stockId);
    }

    @Test
    void testGetCurrentStockPrice() {
        String ticker = "AAPL";
        String mockApiResponse = """
            {
                "Time Series (1min)": {
                    "2024-12-01 10:00:00": { "close": 150.00 }
                }
            }
            """;
        when(alphaVantageApiClient.getStockPrice(ticker)).thenReturn(Mono.just(mockApiResponse));

        StepVerifier.create(stockService.getCurrentStockPrice(ticker))
                .assertNext(priceDTO -> assertEquals(150.00, priceDTO.getClosePrice()))
                .verifyComplete();

        verify(alphaVantageApiClient).getStockPrice(ticker);
    }

    @Test
    void testGetCompanyInfo() {
        String ticker = "AAPL";
        String companyInfo = "Apple Inc. - Technology Giant";
        when(finnuhApiClient.getCompanyInfo(ticker)).thenReturn(Mono.just(companyInfo));

        StepVerifier.create(stockService.getCompanyInfo(ticker))
                .expectNext(companyInfo)
                .verifyComplete();

        verify(finnuhApiClient).getCompanyInfo(ticker);
    }

    @Test
    void testTotalPortfolioValue() {
        Stock stock1 = new Stock("id-3459k-ky76", "Apple Inc", "AAPL", 10, 150.0, false, 1500);
        Stock stock2 = new Stock("id-test-32", "Alphabet Inc", "GOOGL", 5, 2800.0, false, 1400);
        when(stockRepository.findAllByIsDeletedFalse()).thenReturn(Flux.just(stock1, stock2));

        when(alphaVantageApiClient.getStockPrice(stock1.getTicker()))
                .thenReturn(Mono.just("{ \"close\": 160.0 }"));
        when(alphaVantageApiClient.getStockPrice(stock2.getTicker()))
                .thenReturn(Mono.just("{ \"close\": 2900.0 }"));

        StepVerifier.create(stockService.totalPortfolioValue())
                .assertNext(dto -> {
                    assertEquals(14800.0, dto.getInitialPortfolioValue());
                    assertEquals(15300.0, dto.getCurrentPortfolioValue());
                })
                .verifyComplete();

        verify(stockRepository).findAllByIsDeletedFalse();
    }
}
