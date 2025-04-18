package com.frauddetection.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway.rate-limit")
public class RateLimitProperties {
    private int defaultReplenishRate = 10;
    private int defaultBurstCapacity = 20;
    private int defaultRequestedTokens = 1;

    public int getDefaultReplenishRate() {
        return defaultReplenishRate;
    }

    public void setDefaultReplenishRate(int defaultReplenishRate) {
        this.defaultReplenishRate = defaultReplenishRate;
    }

    public int getDefaultBurstCapacity() {
        return defaultBurstCapacity;
    }

    public void setDefaultBurstCapacity(int defaultBurstCapacity) {
        this.defaultBurstCapacity = defaultBurstCapacity;
    }

    public int getDefaultRequestedTokens() {
        return defaultRequestedTokens;
    }

    public void setDefaultRequestedTokens(int defaultRequestedTokens) {
        this.defaultRequestedTokens = defaultRequestedTokens;
    }
}