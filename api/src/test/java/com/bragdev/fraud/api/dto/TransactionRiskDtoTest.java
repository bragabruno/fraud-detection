package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionRiskDtoTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String transactionId = "123e4567-e89b-12d3-a456-426614174000";
        double riskScore = 75.5;
        String riskLevel = "MEDIUM";
        
        // Act
        TransactionRiskDto risk = new TransactionRiskDto();
        risk.setTransactionId(transactionId);
        risk.setRiskScore(riskScore);
        risk.setRiskLevel(riskLevel);
        
        // Assert
        assertEquals(transactionId, risk.getTransactionId());
        assertEquals(riskScore, risk.getRiskScore(), 0.0001);
        assertEquals(riskLevel, risk.getRiskLevel());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TransactionRiskDto risk1 = new TransactionRiskDto();
        risk1.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        risk1.setRiskScore(75.5);
        risk1.setRiskLevel("MEDIUM");
        
        TransactionRiskDto risk2 = new TransactionRiskDto();
        risk2.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        risk2.setRiskScore(75.5);
        risk2.setRiskLevel("MEDIUM");
        
        TransactionRiskDto risk3 = new TransactionRiskDto();
        risk3.setTransactionId("223e4567-e89b-12d3-a456-426614174000");
        risk3.setRiskScore(95.0);
        risk3.setRiskLevel("HIGH");
        
        // Assert
        assertEquals(risk1, risk2);
        assertEquals(risk1.hashCode(), risk2.hashCode());
        
        assertNotEquals(risk1, risk3);
        assertNotEquals(risk1.hashCode(), risk3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        TransactionRiskDto risk = new TransactionRiskDto();
        risk.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        risk.setRiskScore(75.5);
        risk.setRiskLevel("MEDIUM");
        
        // Act
        String toString = risk.toString();
        
        // Assert
        assertTrue(toString.contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(toString.contains("75.5"));
        assertTrue(toString.contains("MEDIUM"));
    }
    
    @Test
    public void testSetters() {
        // Arrange
        TransactionRiskDto risk = new TransactionRiskDto();
        
        // Act - Initial values
        risk.setTransactionId("123e4567-e89b-12d3-a456-426614174000");
        risk.setRiskScore(75.5);
        risk.setRiskLevel("MEDIUM");
        
        // Assert - Initial values
        assertEquals("123e4567-e89b-12d3-a456-426614174000", risk.getTransactionId());
        assertEquals(75.5, risk.getRiskScore(), 0.0001);
        assertEquals("MEDIUM", risk.getRiskLevel());
        
        // Act - Change values
        risk.setTransactionId("223e4567-e89b-12d3-a456-426614174000");
        risk.setRiskScore(95.0);
        risk.setRiskLevel("HIGH");
        
        // Assert - Changed values
        assertEquals("223e4567-e89b-12d3-a456-426614174000", risk.getTransactionId());
        assertEquals(95.0, risk.getRiskScore(), 0.0001);
        assertEquals("HIGH", risk.getRiskLevel());
    }
    
    @Test
    public void testNullValues() {
        // Arrange
        TransactionRiskDto risk = new TransactionRiskDto();
        
        // Act & Assert - Null transaction ID
        risk.setTransactionId(null);
        assertNull(risk.getTransactionId());
        
        // Act & Assert - Null risk level
        risk.setRiskLevel(null);
        assertNull(risk.getRiskLevel());
    }
}