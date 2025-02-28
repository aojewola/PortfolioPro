package com.task.PortfolioPro.config;

import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientTestConfig {


    @Bean
    public WebClient webClient(MockWebServer mockWebServer) {
        return WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }

    @Bean
    public MockWebServer mockWebServer() {
        return new MockWebServer();
    }
}
