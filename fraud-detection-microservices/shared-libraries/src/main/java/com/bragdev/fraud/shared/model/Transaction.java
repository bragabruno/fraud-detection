package com.bragdev.fraud.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a financial transaction in the fraud detection system.
 * This is a shared domain model used across multiple microservices.
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
    private LocalDateTime timestamp;
    private String channel;
    private String transactionType;
    private LocalDateTime receivedAt;
    private String transactionReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
