package com.bragdev.fraud.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a financial transaction in the fraud detection system.
 * Contains all necessary information for fraud analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private Instant timestamp;
    private GeoLocation location;
    private String channel;
    private DeviceInfo deviceInfo;
    private TransactionType type;
    private Map<String, Object> additionalAttributes;
    private Instant receivedAt;
    private String transactionReference;
    
    /**
     * Creates a simple transaction for testing and demo purposes
     */
    public static Transaction createSample() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId("ACC" + (int)(Math.random() * 10000))
                .amount(new BigDecimal(String.format("%.2f", Math.random() * 1000)))
                .currency("USD")
                .merchantId("MCH" + (int)(Math.random() * 1000))
                .merchantName("Sample Merchant")
                .merchantCategory("RETAIL")
                .timestamp(Instant.now())
                .location(new GeoLocation(40.7128, -74.0060))
                .channel("ONLINE")
                .deviceInfo(new DeviceInfo("Chrome", "Windows", "1.2.3.4"))
                .type(TransactionType.PURCHASE)
                .receivedAt(Instant.now())
                .transactionReference("TX" + System.currentTimeMillis())
                .build();
    }
}