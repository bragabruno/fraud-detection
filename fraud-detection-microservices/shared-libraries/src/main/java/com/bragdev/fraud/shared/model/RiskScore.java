package com.bragdev.fraud.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a risk assessment score for a transaction.
 * This is a shared domain model used across multiple microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScore {
    private UUID id;
    private UUID transactionId;
    private double score;
    private RiskLevel riskLevel;
    private List<RuleEvaluation> triggeredRules;
    private LocalDateTime evaluatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing different risk levels.
     */
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
