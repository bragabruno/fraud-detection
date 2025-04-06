package com.bragdev.fraud.plaid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Represents a transaction from Plaid.
 * Maps to transaction data returned from Plaid's API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaidTransaction {
    private String id;
    private String accountId;
    private String pendingTransactionId;
    private String categoryId;
    private String[] category;
    private String merchantName;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private boolean pending;
    private String currencyCode;
    private Location location;
    private PaymentMeta paymentMeta;
    private Map<String, String> additionalProperties;
    
    /**
     * Location information for a transaction
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private String address;
        private String city;
        private String region;
        private String postalCode;
        private String country;
        private Double latitude;
        private Double longitude;
        private String storeNumber;
    }
    
    /**
     * Payment metadata for a transaction
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMeta {
        private String byOrderOf;
        private String payee;
        private String payer;
        private String paymentMethod;
        private String paymentProcessor;
        private String ppdId;
        private String reason;
        private String referenceNumber;
    }
}