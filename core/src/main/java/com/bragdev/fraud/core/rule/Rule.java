package com.bragdev.fraud.core.rule;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;

/**
 * Interface for all fraud detection rules.
 * Each rule evaluates a transaction and determines if it should be triggered.
 */
public interface Rule {
    /**
     * Unique identifier for this rule
     */
    String getId();
    
    /**
     * Human-readable name for this rule
     */
    String getName();
    
    /**
     * Description of what this rule detects
     */
    String getDescription();
    
    /**
     * Category this rule belongs to (e.g., "amount", "velocity", "location")
     */
    String getCategory();
    
    /**
     * How much this rule contributes to the overall risk score when triggered
     * @return A value between 0 and 100
     */
    double getSeverity();
    
    /**
     * Evaluates if the rule should be triggered for the given transaction
     * @param transaction The transaction to evaluate
     * @return true if rule is triggered, false otherwise
     */
    boolean evaluate(Transaction transaction);
    
    /**
     * Creates a TriggeredRule object if this rule is triggered for the transaction
     * @param transaction The transaction to evaluate
     * @return A TriggeredRule if triggered, null otherwise
     */
    default TriggeredRule createTriggeredRule(Transaction transaction) {
        if (evaluate(transaction)) {
            return TriggeredRule.create(
                getId(),
                getName(),
                getSeverity()
            );
        }
        return null;
    }
    
    /**
     * Generates a human-readable explanation of why this rule was triggered
     * @param transaction The transaction that triggered the rule
     * @return A string explaining the trigger reason
     */
    default String generateTriggerReason(Transaction transaction) {
        return String.format("Rule '%s' was triggered for transaction", getName());
    }
}