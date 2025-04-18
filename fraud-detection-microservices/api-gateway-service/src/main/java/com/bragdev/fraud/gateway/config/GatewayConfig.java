package com.bragdev.fraud.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
/**
 * Configuration class for the API Gateway routes.
 * Defines the routing rules for forwarding requests to the appropriate microservices.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RedisRateLimiter rateLimiter) {
        return builder.routes()
                // Transaction Service routes with versioning and rate limiting
                .route("transaction-service-v1", r -> r.path("/api/v1/transactions/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://transaction-service"))
                
                // Rule Engine Service routes
                .route("rule-engine-service-v1", r -> r.path("/api/v1/rules/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://rule-engine-service"))
                
                // Decision Service routes
                .route("decision-service-v1", r -> r.path("/api/v1/decisions/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://decision-service"))
                
                // Data Integration Service routes
                .route("data-integration-service-v1", r -> r.path("/api/v1/integrations/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://data-integration-service"))
                
                // Notification Service routes
                .route("notification-service-v1", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://notification-service"))
                
                // Analytics Service routes
                .route("analytics-service-v1", r -> r.path("/api/v1/analytics/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://analytics-service"))
                
                // User Management Service routes
                .route("user-management-service-v1", r -> r.path("/api/v1/users/**", "/api/v1/auth/**")
                        .filters(f -> f
                            .rewritePath("/api/v1/(?<segment>.*)", "/api/${segment}")
                            .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                            .addResponseHeader("API-Version", "1.0"))
                        .uri("lb://user-management-service"))
                
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("api-user")
                .password("default-secure-password")
                .roles("API_USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
