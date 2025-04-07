package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TriggeredRuleDtoTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String ruleId = "RULE_001";
        String ruleName = "High Value Transaction Rule";
        String category = "AMOUNT";
        double severity = 80.0;
        String triggerReason = "Transaction amount exceeds threshold";
        
        // Act
        TriggeredRuleDto rule = new TriggeredRuleDto();
        rule.setRuleId(ruleId);
        rule.setRuleName(ruleName);
        rule.setCategory(category);
        rule.setSeverity(severity);
        rule.setTriggerReason(triggerReason);
        
        // Assert
        assertEquals(ruleId, rule.getRuleId());
        assertEquals(ruleName, rule.getRuleName());
        assertEquals(category, rule.getCategory());
        assertEquals(severity, rule.getSeverity(), 0.0001);
        assertEquals(triggerReason, rule.getTriggerReason());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TriggeredRuleDto rule1 = new TriggeredRuleDto();
        rule1.setRuleId("RULE_001");
        rule1.setRuleName("High Value Transaction Rule");
        rule1.setCategory("AMOUNT");
        rule1.setSeverity(80.0);
        rule1.setTriggerReason("Transaction amount exceeds threshold");
        
        TriggeredRuleDto rule2 = new TriggeredRuleDto();
        rule2.setRuleId("RULE_001");
        rule2.setRuleName("High Value Transaction Rule");
        rule2.setCategory("AMOUNT");
        rule2.setSeverity(80.0);
        rule2.setTriggerReason("Transaction amount exceeds threshold");
        
        TriggeredRuleDto rule3 = new TriggeredRuleDto();
        rule3.setRuleId("RULE_002");
        rule3.setRuleName("Velocity Check Rule");
        rule3.setCategory("VELOCITY");
        rule3.setSeverity(70.0);
        rule3.setTriggerReason("Multiple transactions in short time period");
        
        // Assert
        assertEquals(rule1, rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
        
        assertNotEquals(rule1, rule3);
        assertNotEquals(rule1.hashCode(), rule3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        TriggeredRuleDto rule = new TriggeredRuleDto();
        rule.setRuleId("RULE_001");
        rule.setRuleName("High Value Transaction Rule");
        rule.setCategory("AMOUNT");
        rule.setSeverity(80.0);
        rule.setTriggerReason("Transaction amount exceeds threshold");
        
        // Act
        String toString = rule.toString();
        
        // Assert
        assertTrue(toString.contains("RULE_001"));
        assertTrue(toString.contains("High Value Transaction Rule"));
        assertTrue(toString.contains("AMOUNT"));
        assertTrue(toString.contains("80.0"));
        assertTrue(toString.contains("Transaction amount exceeds threshold"));
    }
    
    @Test
    public void testSetters() {
        // Arrange
        TriggeredRuleDto rule = new TriggeredRuleDto();
        
        // Act - Initial values
        rule.setRuleId("RULE_001");
        rule.setRuleName("High Value Transaction Rule");
        rule.setCategory("AMOUNT");
        rule.setSeverity(80.0);
        rule.setTriggerReason("Transaction amount exceeds threshold");
        
        // Assert - Initial values
        assertEquals("RULE_001", rule.getRuleId());
        assertEquals("High Value Transaction Rule", rule.getRuleName());
        assertEquals("AMOUNT", rule.getCategory());
        assertEquals(80.0, rule.getSeverity(), 0.0001);
        assertEquals("Transaction amount exceeds threshold", rule.getTriggerReason());
        
        // Act - Change values
        rule.setRuleId("RULE_002");
        rule.setRuleName("Velocity Check Rule");
        rule.setCategory("VELOCITY");
        rule.setSeverity(70.0);
        rule.setTriggerReason("Multiple transactions in short time period");
        
        // Assert - Changed values
        assertEquals("RULE_002", rule.getRuleId());
        assertEquals("Velocity Check Rule", rule.getRuleName());
        assertEquals("VELOCITY", rule.getCategory());
        assertEquals(70.0, rule.getSeverity(), 0.0001);
        assertEquals("Multiple transactions in short time period", rule.getTriggerReason());
    }
    
    @Test
    public void testNullValues() {
        // Arrange
        TriggeredRuleDto rule = new TriggeredRuleDto();
        
        // Act & Assert - Null values
        rule.setRuleId(null);
        rule.setRuleName(null);
        rule.setCategory(null);
        rule.setTriggerReason(null);
        
        assertNull(rule.getRuleId());
        assertNull(rule.getRuleName());
        assertNull(rule.getCategory());
        assertNull(rule.getTriggerReason());
    }
}