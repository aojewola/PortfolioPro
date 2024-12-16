package com.task.portfoliopro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "portfoliopro.finnhub-api")
@Data
public class FinnhubApiConfig {
    private String url;
    private String apiKey;

}
