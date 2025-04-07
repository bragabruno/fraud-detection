package com.bragdev.fraud.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents the evaluation of a specific fraud detection rule.
 * This is a shared domain model used across multiple microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleEvaluation {
    private UUID id;
    private String ruleId;
    private String ruleName;
    private double severity;
    private String reason;
    private boolean triggered;
}
