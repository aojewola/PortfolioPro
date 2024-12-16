package com.task.PortfolioPro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;


import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class H2DatabaseConfigTest {
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "h2")
                .option(PROTOCOL, "mem") // In-memory database
                .option(DATABASE, "testdb")
                .build());
    }
}
