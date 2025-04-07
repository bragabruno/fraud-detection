package com.bragdev.fraud.detection.demo;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import com.bragdev.fraud.core.engine.RuleEngine;
import com.bragdev.fraud.core.engine.SimpleRuleEngine;
import com.bragdev.fraud.core.model.GeoLocation;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.detection.rule.GeographicAnomalyRule;
import com.bragdev.fraud.detection.rule.HighValueTransactionRule;
import com.bragdev.fraud.detection.rule.TimeBasedPatternRule;
import com.bragdev.fraud.detection.rule.UnusualMerchantCategoryRule;
import com.bragdev.fraud.detection.rule.VelocityCheckRule;

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
        
        // Add geographic anomaly rule
        engine.addRule(new GeographicAnomalyRule(
            new GeoLocation(40.7128, -74.0060), // NYC coordinates
            500.0 // 500 km threshold
        ));
        
        // Add velocity check rule
        engine.addRule(new VelocityCheckRule(
            Duration.ofMinutes(5), // 5 minute window
            3 // Max 3 transactions in 5 minutes
        ));
        
        // Add unusual merchant category rule
        engine.addRule(new UnusualMerchantCategoryRule(
            Set.of("GAMBLING", "CRYPTOCURRENCY", "MONEY_TRANSFER")
        ));
        
        // Add time-based pattern rule
        engine.addRule(new TimeBasedPatternRule());
        
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
        
        // Print the evaluation results using reflection to access fields
        try {
            // Access transaction ID
            java.lang.reflect.Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            System.out.println("Transaction ID: " + idField.get(transaction));
            
            // Access risk score fields
            java.lang.reflect.Field overallScoreField = RiskScore.class.getDeclaredField("overallScore");
            overallScoreField.setAccessible(true);
            System.out.println("Risk Score: " + overallScoreField.get(riskScore));
            
            java.lang.reflect.Field riskLevelField = RiskScore.class.getDeclaredField("riskLevel");
            riskLevelField.setAccessible(true);
            System.out.println("Risk Level: " + riskLevelField.get(riskScore));
            
            java.lang.reflect.Field triggeredRulesField = RiskScore.class.getDeclaredField("triggeredRules");
            triggeredRulesField.setAccessible(true);
            List<TriggeredRule> triggeredRules = (List<TriggeredRule>) triggeredRulesField.get(riskScore);
            System.out.println("Triggered Rules: " + (triggeredRules != null ? triggeredRules.size() : 0));
            
            // Print details of triggered rules
            if (triggeredRules != null && !triggeredRules.isEmpty()) {
                System.out.println("\nTriggered Rules Details:");
                for (TriggeredRule rule : triggeredRules) {
                    java.lang.reflect.Field ruleNameField = TriggeredRule.class.getDeclaredField("ruleName");
                    ruleNameField.setAccessible(true);
                    String ruleName = (String) ruleNameField.get(rule);
                    
                    java.lang.reflect.Field severityField = TriggeredRule.class.getDeclaredField("severity");
                    severityField.setAccessible(true);
                    double severity = (double) severityField.get(rule);
                    
                    java.lang.reflect.Field triggerReasonField = TriggeredRule.class.getDeclaredField("triggerReason");
                    triggerReasonField.setAccessible(true);
                    String triggerReason = (String) triggerReasonField.get(rule);
                    
                    System.out.println("- " + ruleName + " (Severity: " + severity + ")");
                    System.out.println("  Reason: " + triggerReason);
                }
            }
        } catch (Exception e) {
            System.out.println("Error accessing fields: " + e.getMessage());
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
        
        // All rules are now implemented and working
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
    }
}