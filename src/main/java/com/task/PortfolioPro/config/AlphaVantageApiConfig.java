package com.task.portfoliopro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "portfoliopro.alpha-vantage-api")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlphaVantageApiConfig {
    private String url;
    private String apiKey;
}
