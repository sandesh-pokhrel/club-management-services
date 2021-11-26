package com.fitness.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("client-service", r -> r.path("/client-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://client-service"))
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                .build();
    }
}
