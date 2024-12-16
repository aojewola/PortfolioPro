package com.task.portfoliopro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableRetry
@EnableCaching
@EnableAsync
public class PortfolioProApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioProApplication.class, args);
	}

}
