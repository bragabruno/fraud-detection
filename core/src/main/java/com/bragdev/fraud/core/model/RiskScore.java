package com.bragdev.fraud.core.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the risk assessment result for a transaction.
 * Contains the overall score and component scores from different detection methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScore {
    private UUID id;
    private UUID transactionId;
    private double overallScore; // 0-100, where higher means higher risk
    private RiskLevel riskLevel;
    private Map<String, Double> componentScores; // Scores from individual detection engines
    private List<TriggeredRule> triggeredRules;
    private Map<String, Object> explanations; // Explanations for high risk assessments
    private double confidenceLevel; // 0-1, confidence in the assessment
    private Instant evaluatedAt;
    private String evaluatedBy; // Component or service that produced this score
    
    @Builder.Default
    private Map<String, Object> additionalInfo = new HashMap<>();
    
    /**
     * Determines the risk level based on the overall score
     */
    public RiskLevel calculateRiskLevel() {
        if (overallScore < 20) {
            return RiskLevel.LOW;
        } else if (overallScore < 50) {
            return RiskLevel.MEDIUM;
        } else if (overallScore < 80) {
            return RiskLevel.HIGH;
        } else {
            return RiskLevel.CRITICAL;
        }
    }
    
    /**
     * Updates the risk level based on the current overall score
     */
    public void updateRiskLevel() {
        this.riskLevel = calculateRiskLevel();
    }
}