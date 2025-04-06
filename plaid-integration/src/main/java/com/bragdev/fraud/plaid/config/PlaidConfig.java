package com.bragdev.fraud.plaid.config;

import com.plaid.client.ApiClient;
import com.plaid.client.api.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Plaid API client.
 * This sets up the Plaid client with credentials from application properties.
 */
@Configuration
public class PlaidConfig {

    @Value("${plaid.client-id}")
    private String clientId;

    @Value("${plaid.secret}")
    private String secret;

    @Value("${plaid.environment}")
    private String environment;

    /**
     * Creates a configured Plaid API client based on application properties.
     *
     * @return Configured PlaidApi instance
     */
    @Bean
    public PlaidApi plaidApi() {
        ApiClient apiClient = new ApiClient();
        
        // Set the base path based on environment
        String basePath;
        switch (environment.toLowerCase()) {
            case "sandbox":
                basePath = "https://sandbox.plaid.com";
                break;
            case "development":
                basePath = "https://development.plaid.com";
                break;
            case "production":
                basePath = "https://production.plaid.com";
                break;
            default:
                basePath = "https://sandbox.plaid.com"; // Default to sandbox
        }
        apiClient.setBasePath(basePath);
        
        // Set default headers for authentication
        Map<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.put("PLAID-CLIENT-ID", clientId);
        defaultHeaders.put("PLAID-SECRET", secret);
        apiClient.setDefaultHeaders(defaultHeaders);
        
        // Create and return the PlaidApi instance
        return new PlaidApi(apiClient);
    }
}