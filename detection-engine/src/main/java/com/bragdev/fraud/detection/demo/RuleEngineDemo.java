package com.bragdev.fraud.detection.demo;

import java.math.BigDecimal;

import com.bragdev.fraud.core.engine.RuleEngine;
import com.bragdev.fraud.core.engine.SimpleRuleEngine;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.detection.rule.HighValueTransactionRule;

/**
 * A demonstration class that shows how to initialize and use the rule engine
 * with different fraud detection rules.
 */
public class RuleEngineDemo {

    /**
     * Creates a configured rule engine with standard fraud detection rules
     * 
     * @return A rule engine with several fraud detection rules
     */
    public static RuleEngine createStandardRuleEngine() {
        // Create a new rule engine
        RuleEngine engine = new SimpleRuleEngine();
        
        // Add high-value transaction rule
        engine.addRule(new HighValueTransactionRule(
            new BigDecimal("1000.00"), 
            "USD"
        ));
        
        // In a real implementation, we would add other rules:
        // Geographic anomaly rule
        // Velocity check rule
        // Unusual merchant category rule
        // Time-based pattern rule
        
        return engine;
    }
    
    /**
     * Demonstrates how to evaluate a transaction using the rule engine
     * 
     * @param transaction The transaction to evaluate
     * @return The risk score result from the evaluation
     */
    public static RiskScore evaluateTransaction(Transaction transaction) {
        RuleEngine engine = createStandardRuleEngine();
        return engine.evaluate(transaction);
    }
    
    /**
     * Main method for command-line demonstration
     */
    public static void main(String[] args) {
        // Create a sample transaction
        Transaction transaction = Transaction.createSample();
        
        // Evaluate the transaction
        RiskScore riskScore = evaluateTransaction(transaction);
        
        // Print the evaluation results
        System.out.println("Transaction ID: " + transaction.getId());
        System.out.println("Risk Score: " + riskScore.getOverallScore());
        System.out.println("Risk Level: " + riskScore.getRiskLevel());
        System.out.println("Triggered Rules: " + riskScore.getTriggeredRules().size());
        
        // Print details of triggered rules
        if (!riskScore.getTriggeredRules().isEmpty()) {
            System.out.println("\nTriggered Rules Details:");
            riskScore.getTriggeredRules().forEach(rule -> {
                System.out.println("- " + rule.getRuleName() + " (Severity: " + rule.getSeverity() + ")");
                System.out.println("  Reason: " + rule.getTriggerReason());
            });
        }
    }
    
    /**
     * Example of how to add specific types of rules when the implementation is fixed
     */
    private static void futureRuleExamples() {
        RuleEngine engine = new SimpleRuleEngine();
        
        // This shows how to add the various types of rules once they've been fixed
        
        // High value transaction rule
        engine.addRule(new HighValueTransactionRule(new BigDecimal("1000.00"), "USD"));
        
        // Future rule examples (commented out since they're not working yet)
        /*
        // Geographic anomaly rule
        engine.addRule(new GeographicAnomalyRule(
            new GeoLocation(40.7128, -74.0060), // NYC coordinates
            500.0 // 500 km threshold
        ));
        
        // Velocity check rule
        engine.addRule(new VelocityCheckRule(
            Duration.ofMinutes(5), // 5 minute window
            3 // Max 3 transactions in 5 minutes
        ));
        
        // Unusual merchant category rule
        engine.addRule(new UnusualMerchantCategoryRule(
            Set.of("GAMBLING", "CRYPTOCURRENCY", "MONEY_TRANSFER")
        ));
        
        // Time-based pattern rule
        engine.addRule(new TimeBasedPatternRule());
        */
    }
}