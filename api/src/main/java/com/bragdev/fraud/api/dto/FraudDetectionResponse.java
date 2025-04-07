package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FraudDetectionResponse {
    private String transactionId;
    private double riskScore;
    private String riskLevel;
    private List<TriggeredRuleDto> triggeredRules;
    private Map<String, Double> componentScores;
    private Map<String, Object> explanations;
    
    // For batch processing
    private List<TransactionRiskDto> results;
}