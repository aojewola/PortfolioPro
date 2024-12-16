package com.task.portfoliopro.apiClients;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.task.portfoliopro.config.FinnhubApiConfig;
import com.task.portfoliopro.dto.CompanyDTO;
import com.task.portfoliopro.dto.FinnhubResponseDTO;
import com.task.portfoliopro.errors.HttpError;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class FinnuhApiClient {

    private final FinnhubApiConfig finnhubApiConfig;

    private final WebClient webClient;

    public FinnuhApiClient(final WebClient.Builder webClientBuilder, final FinnhubApiConfig finnhubApiConfig) {
        this.finnhubApiConfig = finnhubApiConfig;
        this.webClient = webClientBuilder.baseUrl(this.finnhubApiConfig.getUrl()).build();
    }

    public Mono<String> getCompanyInfo(final String ticker) {
        log.info("Sending request to FINNHUB API for stock {}", ticker);
        return webClient.get().uri(uriBuilder -> uriBuilder
                                    .queryParam("q", ticker)
                                    .queryParam("token", finnhubApiConfig.getApiKey())
                                    .build())
                                    .retrieve()
                                    .bodyToMono(FinnhubResponseDTO.class)
                                    .flatMap(respone -> Flux.fromIterable(respone.getResult())
                                    .next()
                                    .map(CompanyDTO::getDescription))
                                    .onErrorMap(exception -> {
                                        log.error("FINNHUB API: error from Finnhub api for stock ticker %s"
                                                .formatted(exception));
                                        return new HttpError(HttpStatus.EXPECTATION_FAILED, exception.getMessage());
                                    }).retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                                    .filter(ex -> ex instanceof RuntimeException))
                                    .subscribeOn(Schedulers.boundedElastic());
    }

}
