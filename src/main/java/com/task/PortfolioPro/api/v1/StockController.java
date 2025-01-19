package com.task.portfoliopro.api.v1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.portfoliopro.dto.PortfolioUpdateDTO;
import com.task.portfoliopro.dto.StockDTO;
import com.task.portfoliopro.dto.StockPriceDTO;
import com.task.portfoliopro.entities.Stock;
import com.task.portfoliopro.services.StockService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;


@RestController
@RequestMapping(value = "/api/v1/portfoliopro", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired
    private StockService stockService;


    @GetMapping("/stocks")
    @Operation(summary = "Retrieve all added stocks", description = "Fetches all available stocks in the db")
    public Flux<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping("/stocks")
    @Operation(summary = "Add new stock to user's portfolio", description = "Allow user to add new stock to their portfolio")
    public Mono<Stock> addStock(@RequestBody final StockDTO stockDto) {
        log.info("Request received to add stock {}", stockDto.getStockName());
        return stockService.addStock(stockDto);
    }

    @DeleteMapping("stocks/{id}")
    @Operation(summary = "Soft delete a stock from the user portfolio", description = "Remove a stock from the user portfolio")
    public Mono<Stock> removeStock(@PathVariable final String id) {
        log.info("Request recived to soft delete stock with id {}", id);
        return stockService.removeStock(id);
    }

    @DeleteMapping("stocks/{id}/delete")
    // @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Hard delete a stock from the user portfolio", description = "Permanently delete a stock from the user portfolio")
    public Mono<Void> deleteStock(@PathVariable final String id) {
        log.info("Request recived to hard delete stock with id {}", id);
        return stockService.deleteStock(id);
    }

    @GetMapping("/total-value")
    @Operation(summary = "Returns the total value of the portfolio", description = "Calculate the total value of the user portfolio")
    public Mono<PortfolioUpdateDTO> getTotalPortfolioValue() {
        log.info("Request received to calculate the total portfolio value for the user");
        return stockService.totalPortfolioValue();
    }

    @GetMapping("/stock-price")
    @Operation(summary = "Returns the real time price data of the given ticker", description = "Fetches the price real data for a given stock")
    public Mono<StockPriceDTO> getRealTimeStockPrice(@RequestParam final String ticker ) {
        log.info("Request received to fetch the real time price data for {}", ticker);
        return stockService.getCurrentStockPrice(ticker);
    }

    @GetMapping("/stocks/time-series")
    @Operation(summary = "Returns the real time series qprice data of the given ticker", description = "Fetches the series price real data for a given stock")
    public Mono<String> getTimeSeriesStockPrice(@RequestParam final String ticker ) {
        log.info("Request received to fetch time series price data for {}", ticker);
        return stockService.stockRealTimeSeries(ticker);
    }

    @GetMapping("/company-info")
    public Mono<String> getCompanyInfo(@RequestParam final String ticker) {
        log.info("Request received to fetch company info for stock ticker {}", ticker);
        return stockService.getCompanyInfo(ticker);
    }
}
