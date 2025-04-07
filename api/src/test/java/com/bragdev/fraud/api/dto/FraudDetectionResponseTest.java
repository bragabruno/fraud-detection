package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FraudDetectionResponseTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String transactionId = "123e4567-e89b-12d3-a456-426614174000";
        double riskScore = 75.5;
        String riskLevel = "MEDIUM";
        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("VELOCITY", 80.0);
        componentScores.put("AMOUNT", 60.0);
        List<String> explanations = new ArrayList<>();
        explanations.add("Unusual transaction amount");
        explanations.add("Multiple transactions in short time period");
        List<TriggeredRuleDto> triggeredRules = new ArrayList<>();
        triggeredRules.add(createTriggeredRule("RULE1", "High Value Rule", 80.0));
        List<TransactionRiskDto> results = new ArrayList<>();
        results.add(createTransactionRisk("TX1", 75.5, "MEDIUM"));
        
        // Act
        FraudDetectionResponse response = new FraudDetectionResponse();
        response.setTransactionId(transactionId);
        response.setRiskScore(riskScore);
        response.setRiskLevel(riskLevel);
        response.setComponentScores(componentScores);
        response.setExplanations(explanations);
        response.setTriggeredRules(triggeredRules);
        response.setResults(results);
        
        // Assert
        assertEquals(transactionId, response.getTransactionId());
        assertEquals(riskScore, response.getRiskScore(), 0.0001);
        assertEquals(riskLevel, response.getRiskLevel());
        assertEquals(componentScores, response.getComponentScores());
        assertEquals(explanations, response.getExplanations());
        assertEquals(triggeredRules, response.getTriggeredRules());
        assertEquals(results, response.getResults());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FraudDetectionResponse response1 = new FraudDetectionResponse();
        response1.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        response1.setRiskScore(75.5);
        response1.setRiskLevel("MEDIUM");
        
        FraudDetectionResponse response2 = new FraudDetectionResponse();
        response2.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        response2.setRiskScore(75.5);
        response2.setRiskLevel("MEDIUM");
        
        FraudDetectionResponse response3 = new FraudDetectionResponse();
        response3.setTransactionId("223e4567-e89b-12d3-a456-426614174000");
        response3.setRiskScore(95.0);
        response3.setRiskLevel("HIGH");
        
        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        FraudDetectionResponse response = new FraudDetectionResponse();
        response.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        response.setRiskScore(75.5);
        response.setRiskLevel("MEDIUM");
        
        // Act
        String toString = response.toString();
        
        // Assert
        assertTrue(toString.contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(toString.contains("75.5"));
        assertTrue(toString.contains("MEDIUM"));
    }
    
    @Test
    public void testCollectionProperties() {
        // Arrange
        FraudDetectionResponse response = new FraudDetectionResponse();
        
        // Test component scores
        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("VELOCITY", 80.0);
        componentScores.put("AMOUNT", 60.0);
        response.setComponentScores(componentScores);
        assertEquals(componentScores, response.getComponentScores());
        assertEquals(2, response.getComponentScores().size());
        assertEquals(80.0, response.getComponentScores().get("VELOCITY"), 0.0001);
        
        // Test explanations
        List<String> explanations = new ArrayList<>();
        explanations.add("Unusual transaction amount");
        explanations.add("Multiple transactions in short time period");
        response.setExplanations(explanations);
        assertEquals(explanations, response.getExplanations());
        assertEquals(2, response.getExplanations().size());
        
        // Test triggered rules
        List<TriggeredRuleDto> triggeredRules = new ArrayList<>();
        triggeredRules.add(createTriggeredRule("RULE1", "High Value Rule", 80.0));
        triggeredRules.add(createTriggeredRule("RULE2", "Velocity Check Rule", 70.0));
        response.setTriggeredRules(triggeredRules);
        assertEquals(triggeredRules, response.getTriggeredRules());
        assertEquals(2, response.getTriggeredRules().size());
        
        // Test results
        List<TransactionRiskDto> results = new ArrayList<>();
        results.add(createTransactionRisk("TX1", 75.5, "MEDIUM"));
        results.add(createTransactionRisk("TX2", 95.0, "HIGH"));
        response.setResults(results);
        assertEquals(results, response.getResults());
        assertEquals(2, response.getResults().size());
    }
    
    // Helper method to create a triggered rule
    private TriggeredRuleDto createTriggeredRule(String ruleId, String ruleName, double severity) {
        TriggeredRuleDto rule = new TriggeredRuleDto();
        rule.setRuleId(ruleId);
        rule.setRuleName(ruleName);
        rule.setSeverity(severity);
        rule.setCategory("TEST");
        rule.setTriggerReason("Test trigger reason");
        return rule;
    }
    
    // Helper method to create a transaction risk
    private TransactionRiskDto createTransactionRisk(String transactionId, double riskScore, String riskLevel) {
        TransactionRiskDto risk = new TransactionRiskDto();
        risk.setTransactionId(transactionId);
        risk.setRiskScore(riskScore);
        risk.setRiskLevel(riskLevel);
        return risk;
    }
}