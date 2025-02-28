package com.task.PortfolioPro.apiClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.task.portfoliopro.apiClients.FinnuhApiClient;
import com.task.portfoliopro.config.FinnhubApiConfig;
import com.task.portfoliopro.dto.CompanyDTO;
import com.task.portfoliopro.dto.FinnhubResponseDTO;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringJUnitConfig
public class FinnhubApiClientTest {
    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private FinnhubApiConfig finnhubApiConfig;

    private FinnuhApiClient finnhubApiClient;

    @BeforeEach
    void setUp() {

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(finnhubApiConfig.getUrl()).thenReturn("http://mock-finnhub-url.com");
        when(finnhubApiConfig.getApiKey()).thenReturn("mock-api-key");

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        finnhubApiClient = new FinnuhApiClient(webClientBuilder, finnhubApiConfig);
    }

    @Test
    void testGetCompanyInfo_Success() {
       
        FinnhubResponseDTO mockResponse = new FinnhubResponseDTO();
        mockResponse.setResult(List.of(new CompanyDTO("APPL", "APPLE", "NYSE", "")));

     
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FinnhubResponseDTO.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(finnhubApiClient.getCompanyInfo("AAPL"))
                .expectNext("APPL")
                .verifyComplete();

        // Verify the interactions with WebClient
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(FinnhubResponseDTO.class);
    }

    @Test
    void testGetCompanyInfo_ErrorHandling() {
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FinnhubResponseDTO.class)).thenReturn(Mono.error(new RuntimeException("API error")));

       
        StepVerifier.create(finnhubApiClient.getCompanyInfo("AAPL")).expectError()
        .verifyThenAssertThat();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(FinnhubResponseDTO.class);
    }

    @Test
    void testGetCompanyInfo_RetryMechanism() {
        // Mock a failing response initially
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FinnhubResponseDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Temporary error")))
                .thenReturn(Mono.just(new FinnhubResponseDTO(12, List.of(new CompanyDTO("APPL", "APPLE", "NYSE", "")))));

        StepVerifier.create(finnhubApiClient.getCompanyInfo("AAPL"))
                .expectError()
                .verify();

        verify(webClient, times(1)).get();
        verify(responseSpec, times(1)).bodyToMono(FinnhubResponseDTO.class); // Retry happens
    }
}
