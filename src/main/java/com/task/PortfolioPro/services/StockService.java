package com.task.portfoliopro.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.task.portfoliopro.apiClients.AlphaVantageApiClient;
import com.task.portfoliopro.apiClients.FinnuhApiClient;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.dto.StockPriceDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.errors.HttpError;
import com.task.portfoliopro.repository.StockRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AlphaVantageApiClient alpha;

    @Autowired
    private FinnuhApiClient finnuhApiClient;

    public Flux<Stock> getAllStocks() {
        log.info("Fetching all stocks in the database");
        return stockRepository.findAllByIsDeletedFalse();
    }

    public Mono<Stock> addStock(final StockDTO stockDto) {
        log.info("Added a new {} stock", stockDto.getStockName());
        return stockRepository.save(new Stock(stockDto));
    }

    public Mono<Stock> removeStock(final String id) {
        return stockRepository.findByIdAndIsDeletedFalse(id)
            .flatMap(stock -> stockRepository.softDeleteById(id)
                .thenReturn(stock)
        ).switchIfEmpty(Mono.defer(() -> 
            Mono.error(new HttpError(HttpStatus.NOT_FOUND, "No stock found with the id %s".formatted(id)))))
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteStock(final String id) {
        log.info("Permanently delete a stock from a user's porfolio");
        return stockRepository.deleteById(id);
    }

    public Mono<Double> calculateTotalValue() {
        log.info("Calculating total portforlio value");
        return stockRepository.findAllByIsDeletedFalse()
        .map(stock -> stock.getPrice() * stock.getShares())
        .reduce(0.0, Double::sum).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> stockRealTimeSeries(final String ticker) {
        log.info("Sending request to alpha vantage api to get {} stock price", ticker);
        return alpha.getStockPrice(ticker);
    }


    public Mono<StockPriceDTO> getCurrentStockPrice(final String symbol) {
        return alpha.getStockPrice(symbol)
            .map(response -> {
                log.info("Response received from alpha vantage api for {}", symbol);
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode timeSeries = root.path("Time Series (1min)");
                    return new StockPriceDTO(timeSeries);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing stock price data", e);
                }
            });
    }

    public Mono<String> getCompanyInfo(final String symbol) {
        return finnuhApiClient.getCompanyInfo(symbol);
    }
}
