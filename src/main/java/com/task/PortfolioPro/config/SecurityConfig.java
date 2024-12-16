// package com.task.portfoliopro.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.web.server.SecurityWebFilterChain;

// @Configuration
// @EnableWebFluxSecurity
// public class SecurityConfig {

// 	@Bean
// 	public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
// 		return http
// 			.authorizeExchange(exchanges -> exchanges
//             .pathMatchers(HttpMethod.OPTIONS, "/**")
//             .permitAll()
// 			    .anyExchange().authenticated()
// 			)
// 			.httpBasic(Customizer.withDefaults())
// 			.formLogin(Customizer.withDefaults())
//             .build();
// 	}
// }
