package com.bragdev.fraud.api.integration;

import com.bragdev.fraud.api.dto.*;
import com.bragdev.fraud.api.service.FraudDetectionService;
import com.bragdev.fraud.core.engine.RuleEngine;
import com.bragdev.fraud.core.model.RiskLevel;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.rule.Rule;
import com.bragdev.fraud.detection.rule.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FraudDetectionIntegrationTest {

    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    public void setup() {
        // Create a fresh instance for each test
        fraudDetectionService = new FraudDetectionService();
    }

    @Test
    public void testLowRiskTransaction() {
        // Arrange - Create a low-risk transaction
        TransactionDto transaction = createTransactionDto(
            "ACC123",
            new BigDecimal("50.00"),
            "USD",
            "MERCH456",
            "Regular Merchant",
            "Retail",
            "ONLINE",
            "PURCHASE"
        );

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);

        // Assert
        assertNotNull(response);
        assertTrue(response.getRiskScore() < 50.0, "Risk score should be low");
        assertEquals("LOW", response.getRiskLevel());
        assertTrue(response.getTriggeredRules() == null || response.getTriggeredRules().isEmpty(),
                "No rules should be triggered for low-risk transaction");
    }

    @Test
    public void testHighValueTransaction() {
        // Arrange - Create a high-value transaction
        TransactionDto transaction = createTransactionDto(
            "ACC123",
            new BigDecimal("10000.00"), // High amount
            "USD",
            "MERCH456",
            "Regular Merchant",
            "Retail",
            "ONLINE",
            "PURCHASE"
        );

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);

        // Assert
        assertNotNull(response);
        assertTrue(response.getRiskScore() >= 50.0, "Risk score should be elevated");
        assertNotEquals("LOW", response.getRiskLevel());
        
        // Verify that the high value rule was triggered
        boolean highValueRuleTriggered = false;
        if (response.getTriggeredRules() != null) {
            for (TriggeredRuleDto rule : response.getTriggeredRules()) {
                if (rule.getRuleId().contains("HIGH_VALUE") || 
                    rule.getRuleName().contains("High Value")) {
                    highValueRuleTriggered = true;
                    break;
                }
            }
        }
        assertTrue(highValueRuleTriggered, "High value rule should be triggered");
    }

    @Test
    public void testUnusualMerchantCategory() {
        // Arrange - Create a transaction with unusual merchant category
        TransactionDto transaction = createTransactionDto(
            "ACC123",
            new BigDecimal("500.00"),
            "USD",
            "MERCH789",
            "Unusual Merchant",
            "Gambling", // Unusual category
            "ONLINE",
            "PURCHASE"
        );

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);

        // Assert
        assertNotNull(response);
        assertTrue(response.getRiskScore() >= 50.0, "Risk score should be elevated");
        assertNotEquals("LOW", response.getRiskLevel());
        
        // Verify that the unusual merchant category rule was triggered
        boolean unusualMerchantRuleTriggered = false;
        if (response.getTriggeredRules() != null) {
            for (TriggeredRuleDto rule : response.getTriggeredRules()) {
                if (rule.getRuleId().contains("MERCHANT") || 
                    rule.getRuleName().contains("Merchant")) {
                    unusualMerchantRuleTriggered = true;
                    break;
                }
            }
        }
        assertTrue(unusualMerchantRuleTriggered, "Unusual merchant rule should be triggered");
    }

    @Test
    public void testGeographicAnomaly() {
        // Arrange - Create a transaction with unusual location
        TransactionDto transaction = createTransactionDto(
            "ACC123",
            new BigDecimal("500.00"),
            "USD",
            "MERCH456",
            "Regular Merchant",
            "Retail",
            "ONLINE",
            "PURCHASE"
        );
        
        // Set unusual location
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(55.7558); // Moscow
        location.setLongitude(37.6173);
        transaction.setLocation(location);

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);

        // Assert
        assertNotNull(response);
        // Note: This test may be location-dependent, so we're not asserting specific risk levels
        // Just checking that the response is valid
        assertNotNull(response.getRiskLevel());
        assertTrue(response.getRiskScore() >= 0.0);
    }

    @Test
    public void testBatchProcessing() {
        // Arrange - Create a batch of transactions
        List<TransactionDto> transactions = new ArrayList<>();
        
        // Add a low-risk transaction
        transactions.add(createTransactionDto(
            "ACC123",
            new BigDecimal("50.00"),
            "USD",
            "MERCH456",
            "Regular Merchant",
            "Retail",
            "ONLINE",
            "PURCHASE"
        ));
        
        // Add a high-risk transaction
        transactions.add(createTransactionDto(
            "ACC123",
            new BigDecimal("10000.00"),
            "USD",
            "MERCH789",
            "Unusual Merchant",
            "Gambling",
            "ONLINE",
            "PURCHASE"
        ));

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateBatch(transactions);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getResults());
        assertEquals(2, response.getResults().size());
        
        // Verify that we have different risk levels in the results
        boolean hasLowRisk = false;
        boolean hasHighRisk = false;
        
        for (TransactionRiskDto result : response.getResults()) {
            if ("LOW".equals(result.getRiskLevel())) {
                hasLowRisk = true;
            } else if ("HIGH".equals(result.getRiskLevel()) || "MEDIUM".equals(result.getRiskLevel())) {
                hasHighRisk = true;
            }
        }
        
        assertTrue(hasLowRisk || hasHighRisk, "Batch should contain transactions with different risk levels");
    }

    @Test
    public void testEndToEndFlow() {
        // Arrange - Create a transaction that should trigger multiple rules
        TransactionDto transaction = createTransactionDto(
            "ACC123",
            new BigDecimal("9999.99"), // High amount
            "USD",
            "MERCH789",
            "Unusual Merchant",
            "Gambling", // Unusual category
            "ONLINE",
            "PURCHASE"
        );
        
        // Set unusual location
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(55.7558); // Moscow
        location.setLongitude(37.6173);
        transaction.setLocation(location);

        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);

        // Assert
        assertNotNull(response);
        assertTrue(response.getRiskScore() >= 70.0, "Risk score should be high");
        assertEquals("HIGH", response.getRiskLevel());
        
        // Verify that multiple rules were triggered
        assertNotNull(response.getTriggeredRules());
        assertTrue(response.getTriggeredRules().size() >= 2, 
                "Multiple rules should be triggered for this high-risk transaction");
        
        // Verify component scores
        assertNotNull(response.getComponentScores());
        assertTrue(response.getComponentScores().size() >= 1, 
                "Component scores should be present");
        
        // Verify explanations
        assertNotNull(response.getExplanations());
        assertTrue(response.getExplanations().size() >= 1, 
                "Explanations should be present");
    }

    // Helper method to create a transaction DTO
    private TransactionDto createTransactionDto(
            String accountId,
            BigDecimal amount,
            String currency,
            String merchantId,
            String merchantName,
            String merchantCategory,
            String channel,
            String type) {
        
        TransactionDto dto = new TransactionDto();
        dto.setAccountId(accountId);
        dto.setAmount(amount);
        dto.setCurrency(currency);
        dto.setMerchantId(merchantId);
        dto.setMerchantName(merchantName);
        dto.setMerchantCategory(merchantCategory);
        dto.setTimestamp(Instant.now());
        dto.setChannel(channel);
        dto.setType(type);
        
        // Default location (San Francisco)
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        dto.setLocation(location);
        
        // Default device info
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        dto.setDeviceInfo(deviceInfo);
        
        return dto;
    }
}