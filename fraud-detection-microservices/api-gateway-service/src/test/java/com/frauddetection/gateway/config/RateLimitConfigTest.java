package com.frauddetection.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitConfigTest {

    private final RateLimitConfig config = new RateLimitConfig();

    @Test
    void redisRateLimiter_ShouldConfigureCorrectLimits() {
        RedisRateLimiter limiter = config.redisRateLimiter();
        
        assertNotNull(limiter);
        assertEquals(10, limiter.getReplenishRate());
        assertEquals(20, limiter.getBurstCapacity());
    }

    @Test
    void userKeyResolver_WithUserHeader_ShouldUseHeaderValue() {
        KeyResolver resolver = config.userKeyResolver();
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("User", "testUser")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = resolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .expectNext("testUser")
                .verifyComplete();
    }

    @Test
    void userKeyResolver_WithoutUserHeader_ShouldUseIpAddress() {
        KeyResolver resolver = config.userKeyResolver();
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = resolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .expectNextMatches(key -> key.equals(
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()))
                .verifyComplete();
    }
}