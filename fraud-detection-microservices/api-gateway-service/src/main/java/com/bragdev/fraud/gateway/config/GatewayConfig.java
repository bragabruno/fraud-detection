package com.bragdev.fraud.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the API Gateway routes.
 * Defines the routing rules for forwarding requests to the appropriate microservices.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Transaction Service routes
                .route("transaction-service", r -> r.path("/api/transactions/**")
                        .uri("lb://transaction-service"))
                
                // Rule Engine Service routes
                .route("rule-engine-service", r -> r.path("/api/rules/**")
                        .uri("lb://rule-engine-service"))
                
                // Decision Service routes
                .route("decision-service", r -> r.path("/api/decisions/**")
                        .uri("lb://decision-service"))
                
                // Data Integration Service routes
                .route("data-integration-service", r -> r.path("/api/integrations/**")
                        .uri("lb://data-integration-service"))
                
                // Notification Service routes
                .route("notification-service", r -> r.path("/api/notifications/**")
                        .uri("lb://notification-service"))
                
                // Analytics Service routes
                .route("analytics-service", r -> r.path("/api/analytics/**")
                        .uri("lb://analytics-service"))
                
                // User Management Service routes
                .route("user-management-service", r -> r.path("/api/users/**", "/api/auth/**")
                        .uri("lb://user-management-service"))
                
                .build();
    }
}
