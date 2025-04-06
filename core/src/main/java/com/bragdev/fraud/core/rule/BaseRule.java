package com.bragdev.fraud.core.rule;

import com.bragdev.fraud.core.model.Transaction;
import lombok.Getter;

/**
 * Base implementation of the Rule interface that provides common functionality.
 * Specific rules can extend this class to implement their evaluation logic.
 */
@Getter
public abstract class BaseRule implements Rule {
    private final String id;
    private final String name;
    private final String description;
    private final String category;
    private final double severity;

    /**
     * Constructor to initialize a rule with all required properties
     */
    protected BaseRule(String id, String name, String description, String category, double severity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        
        // Ensure severity is within valid range
        if (severity < 0 || severity > 100) {
            throw new IllegalArgumentException("Severity must be between 0 and 100");
        }
        this.severity = severity;
    }

    /**
     * The specific evaluation logic to be implemented by concrete rules
     */
    @Override
    public abstract boolean evaluate(Transaction transaction);
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        // Default implementation that can be overridden by specific rules
        return String.format("Rule '%s' was triggered", getName());
    }
}