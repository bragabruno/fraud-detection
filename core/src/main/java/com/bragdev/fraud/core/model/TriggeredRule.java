package com.bragdev.fraud.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a rule that was triggered during transaction evaluation.
 * Contains information about the rule and its contribution to the risk score.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggeredRule {
    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private double severity; // 0-100, where higher means more severe
    private double confidence; // 0-1, confidence that this rule is correctly triggered
    private String category; // Category the rule belongs to (e.g., "velocity", "geo", "amount")
    private String triggerReason; // Human-readable explanation of why the rule triggered
    
    /**
     * Create a simple rule trigger
     */
    public static TriggeredRule create(String ruleId, String ruleName, double severity) {
        return TriggeredRule.builder()
                .ruleId(ruleId)
                .ruleName(ruleName)
                .severity(severity)
                .confidence(1.0)
                .build();
    }
}