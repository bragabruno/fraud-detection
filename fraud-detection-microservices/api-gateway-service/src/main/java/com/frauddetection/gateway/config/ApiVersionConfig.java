package com.frauddetection.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class ApiVersionConfig {

    @Bean
    public RouteLocator versionedRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // V1 API routes
            .route("v1_route", r -> r
                .path("/api/v1/**")
                .filters(f -> f
                    .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                    .addResponseHeader("API-Version", "1.0"))
                .uri("lb://fraud-detection-service"))
            
            // V2 API routes (for future use)
            .route("v2_route", r -> r
                .path("/api/v2/**")
                .filters(f -> f
                    .rewritePath("/api/v2/(?<segment>.*)", "/api/${segment}")
                    .addResponseHeader("API-Version", "2.0"))
                .uri("lb://fraud-detection-service"))
            
            // Default route (latest version)
            .route("default_route", r -> r
                .path("/api/**")
                .filters(f -> f
                    .addResponseHeader("API-Version", "1.0"))
                .uri("lb://fraud-detection-service"))
            .build();
    }

    @Bean
    public VersionHeaderFilter versionHeaderFilter() {
        return new VersionHeaderFilter();
    }
}