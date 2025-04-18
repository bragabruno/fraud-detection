package com.bragdev.fraud.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfiguration {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // Configure rate limiting: 10 requests per second with burst of 20
        return new RedisRateLimiter(10, 20);
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String user = exchange.getRequest().getHeaders().getFirst("User");
            if (user == null) {
                user = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            }
            String finalUser = user;
            return Mono.just(finalUser);
        };
    }
}