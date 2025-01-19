package com.task.portfoliopro.apiClients;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.task.portfoliopro.config.AlphaVantageApiConfig;
import com.task.portfoliopro.errors.HttpError;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class AlphaVantageApiClient {


    private final AlphaVantageApiConfig alpha;

    private final WebClient webClient;

    public AlphaVantageApiClient(final WebClient.Builder webClientBuilder, final AlphaVantageApiConfig alpha) {
        this.alpha  = alpha;
        this.webClient = webClientBuilder.baseUrl(this.alpha.getUrl()).build();
    }

    public Mono<String> getStockPrice(final String ticker) {
        log.info("Sending request to Alpha Vantage API for stock {}", ticker);
        return webClient.get().uri(uriBuilder -> uriBuilder
                                    .queryParam("symbol", ticker)
                                    .queryParam("function", "TIME_SERIES_INTRADAY")
                                    .queryParam("interval", "1min")
                                    .queryParam("apikey", alpha.getApiKey())
                                    .build())
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .onErrorMap(exception -> {
                                        log.error("ALPHA VANTAGE API: error from alpha vantage api for stock ticker %s"
                                                .formatted(exception));
                                        return new HttpError(HttpStatus.EXPECTATION_FAILED, exception.getMessage());
                                    }).retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                                    .filter(ex -> ex instanceof RuntimeException))
                                    .subscribeOn(Schedulers.boundedElastic());
    }
}
