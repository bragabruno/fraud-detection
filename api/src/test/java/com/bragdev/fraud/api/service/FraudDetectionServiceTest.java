package com.bragdev.fraud.api.service;

import com.bragdev.fraud.api.dto.*;
import com.bragdev.fraud.core.engine.RuleEngine;
import com.bragdev.fraud.core.model.RiskLevel;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.decision.DecisionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

public class FraudDetectionServiceTest {

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private DecisionManager decisionManager;

    @InjectMocks
    @Spy
    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Use reflection to set the mocked dependencies
        try {
            java.lang.reflect.Field ruleEngineField = FraudDetectionService.class.getDeclaredField("ruleEngine");
            ruleEngineField.setAccessible(true);
            ruleEngineField.set(fraudDetectionService, ruleEngine);
            
            java.lang.reflect.Field decisionManagerField = FraudDetectionService.class.getDeclaredField("decisionManager");
            decisionManagerField.setAccessible(true);
            decisionManagerField.set(fraudDetectionService, decisionManager);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }
    }

    @Test
    public void testEvaluateTransaction() {
        // Arrange
        TransactionDto transactionDto = createSampleTransactionDto();
        RiskScore mockRiskScore = createMockRiskScore();
        
        // Mock rule engine and decision manager behavior
        when(ruleEngine.evaluate(any(Transaction.class))).thenReturn(mockRiskScore);
        when(decisionManager.makeDecision(any(Transaction.class), anyMap())).thenReturn(mockRiskScore);
        
        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transactionDto);
        
        // Assert
        assertNotNull(response);
        assertEquals(mockRiskScore.getTransactionId().toString(), response.getTransactionId());
        assertEquals(mockRiskScore.getOverallScore(), response.getRiskScore());
        assertEquals(mockRiskScore.getRiskLevel().toString(), response.getRiskLevel());
        assertEquals(mockRiskScore.getComponentScores(), response.getComponentScores());
        assertEquals(mockRiskScore.getExplanations(), response.getExplanations());
        assertEquals(1, response.getTriggeredRules().size());
        
        // Verify interactions
        verify(ruleEngine).evaluate(any(Transaction.class));
        verify(decisionManager).makeDecision(any(Transaction.class), anyMap());
    }

    @Test
    public void testEvaluateBatch() {
        // Arrange
        List<TransactionDto> transactionDtos = Arrays.asList(
            createSampleTransactionDto(),
            createSampleTransactionDto()
        );
        RiskScore mockRiskScore = createMockRiskScore();
        
        // Mock rule engine and decision manager behavior
        when(ruleEngine.evaluate(any(Transaction.class))).thenReturn(mockRiskScore);
        when(decisionManager.makeDecision(any(Transaction.class), anyMap())).thenReturn(mockRiskScore);
        
        // Act
        FraudDetectionResponse response = fraudDetectionService.evaluateBatch(transactionDtos);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getResults());
        assertEquals(2, response.getResults().size());
        
        // Verify each result
        for (TransactionRiskDto result : response.getResults()) {
            assertEquals(mockRiskScore.getTransactionId().toString(), result.getTransactionId());
            assertEquals(mockRiskScore.getOverallScore(), result.getRiskScore());
            assertEquals(mockRiskScore.getRiskLevel().toString(), result.getRiskLevel());
        }
        
        // Verify interactions
        verify(ruleEngine, times(2)).evaluate(any(Transaction.class));
        verify(decisionManager, times(2)).makeDecision(any(Transaction.class), anyMap());
    }

    @Test
    public void testEvaluateBatchWithEmptyList() {
        // Arrange
        List<TransactionDto> emptyList = Collections.emptyList();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            fraudDetectionService.evaluateBatch(emptyList);
        });
    }

    @Test
    public void testEvaluateBatchWithNullList() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            fraudDetectionService.evaluateBatch(null);
        });
    }

    @Test
    public void testConvertToTransaction() {
        // Arrange
        TransactionDto dto = createSampleTransactionDto();
        
        // Act
        Transaction transaction = invokeConvertToTransaction(dto);
        
        // Assert
        assertNotNull(transaction);
        assertEquals(UUID.fromString(dto.getId()), transaction.getId());
        assertEquals(dto.getAccountId(), transaction.getAccountId());
        assertEquals(dto.getAmount(), transaction.getAmount());
        assertEquals(dto.getCurrency(), transaction.getCurrency());
        assertEquals(dto.getMerchantId(), transaction.getMerchantId());
        assertEquals(dto.getMerchantName(), transaction.getMerchantName());
        assertEquals(dto.getMerchantCategory(), transaction.getMerchantCategory());
        assertEquals(dto.getTimestamp(), transaction.getTimestamp());
        assertEquals(dto.getChannel(), transaction.getChannel());
        assertEquals(dto.getType(), transaction.getType().toString());
        assertEquals(dto.getAdditionalAttributes(), transaction.getAdditionalAttributes());
        
        // Verify nested objects
        assertNotNull(transaction.getLocation());
        assertEquals(dto.getLocation().getLatitude(), transaction.getLocation().getLatitude(), 0.0001);
        assertEquals(dto.getLocation().getLongitude(), transaction.getLocation().getLongitude(), 0.0001);
        
        assertNotNull(transaction.getDeviceInfo());
        assertEquals(dto.getDeviceInfo().getBrowser(), transaction.getDeviceInfo().getBrowser());
        assertEquals(dto.getDeviceInfo().getOperatingSystem(), transaction.getDeviceInfo().getOperatingSystem());
        assertEquals(dto.getDeviceInfo().getIpAddress(), transaction.getDeviceInfo().getIpAddress());
    }

    @Test
    public void testConvertToTransactionWithNullDto() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            invokeConvertToTransaction(null);
        });
    }

    @Test
    public void testConvertToResponse() {
        // Arrange
        RiskScore riskScore = createMockRiskScore();
        
        // Act
        FraudDetectionResponse response = invokeConvertToResponse(riskScore);
        
        // Assert
        assertNotNull(response);
        assertEquals(riskScore.getTransactionId().toString(), response.getTransactionId());
        assertEquals(riskScore.getOverallScore(), response.getRiskScore());
        assertEquals(riskScore.getRiskLevel().toString(), response.getRiskLevel());
        assertEquals(riskScore.getComponentScores(), response.getComponentScores());
        assertEquals(riskScore.getExplanations(), response.getExplanations());
        
        // Verify triggered rules
        assertNotNull(response.getTriggeredRules());
        assertEquals(1, response.getTriggeredRules().size());
        TriggeredRuleDto triggeredRuleDto = response.getTriggeredRules().get(0);
        assertEquals("RULE_001", triggeredRuleDto.getRuleId());
        assertEquals("High Value Transaction Rule", triggeredRuleDto.getRuleName());
        assertEquals(80.0, triggeredRuleDto.getSeverity(), 0.0001);
    }

    @Test
    public void testConvertToResponseWithNullRiskScore() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            invokeConvertToResponse(null);
        });
    }

    @Test
    public void testConvertToTriggeredRuleDto() {
        // Arrange
        TriggeredRule rule = TriggeredRule.create("RULE_001", "High Value Transaction Rule", 80.0);
        rule.setCategory("AMOUNT");
        rule.setTriggerReason("Transaction amount exceeds threshold");
        
        // Act
        TriggeredRuleDto dto = invokeConvertToTriggeredRuleDto(rule);
        
        // Assert
        assertNotNull(dto);
        assertEquals(rule.getRuleId(), dto.getRuleId());
        assertEquals(rule.getRuleName(), dto.getRuleName());
        assertEquals(rule.getCategory(), dto.getCategory());
        assertEquals(rule.getSeverity(), dto.getSeverity(), 0.0001);
        assertEquals(rule.getTriggerReason(), dto.getTriggerReason());
    }

    // Helper methods
    private TransactionDto createSampleTransactionDto() {
        TransactionDto dto = new TransactionDto();
        dto.setId(UUID.randomUUID().toString());
        dto.setAccountId("ACC123");
        dto.setAmount(new BigDecimal("100.50"));
        dto.setCurrency("USD");
        dto.setMerchantId("MERCH456");
        dto.setMerchantName("Test Merchant");
        dto.setMerchantCategory("Retail");
        dto.setTimestamp(Instant.now());
        
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        dto.setLocation(location);
        
        dto.setChannel("ONLINE");
        
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        dto.setDeviceInfo(deviceInfo);
        
        dto.setType("PURCHASE");
        
        Map<String, Object> additionalAttributes = new HashMap<>();
        additionalAttributes.put("key1", "value1");
        dto.setAdditionalAttributes(additionalAttributes);
        
        return dto;
    }

    private RiskScore createMockRiskScore() {
        UUID transactionId = UUID.randomUUID();
        double overallScore = 75.5;
        RiskLevel riskLevel = RiskLevel.MEDIUM;
        
        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("VELOCITY", 80.0);
        componentScores.put("AMOUNT", 60.0);
        
        List<String> explanations = new ArrayList<>();
        explanations.add("Unusual transaction amount");
        explanations.add("Multiple transactions in short time period");
        
        List<TriggeredRule> triggeredRules = new ArrayList<>();
        TriggeredRule rule = TriggeredRule.create("RULE_001", "High Value Transaction Rule", 80.0);
        rule.setCategory("AMOUNT");
        rule.setTriggerReason("Transaction amount exceeds threshold");
        triggeredRules.add(rule);
        
        RiskScore riskScore = new RiskScore(transactionId, overallScore, riskLevel);
        riskScore.setComponentScores(componentScores);
        riskScore.setExplanations(explanations);
        riskScore.setTriggeredRules(triggeredRules);
        
        return riskScore;
    }

    // Helper methods to invoke private methods using reflection
    private Transaction invokeConvertToTransaction(TransactionDto dto) {
        try {
            java.lang.reflect.Method method = FraudDetectionService.class.getDeclaredMethod("convertToTransaction", TransactionDto.class);
            method.setAccessible(true);
            return (Transaction) method.invoke(fraudDetectionService, dto);
        } catch (Exception e) {
            fail("Failed to invoke convertToTransaction: " + e.getMessage());
            return null;
        }
    }

    private FraudDetectionResponse invokeConvertToResponse(RiskScore score) {
        try {
            java.lang.reflect.Method method = FraudDetectionService.class.getDeclaredMethod("convertToResponse", RiskScore.class);
            method.setAccessible(true);
            return (FraudDetectionResponse) method.invoke(fraudDetectionService, score);
        } catch (Exception e) {
            fail("Failed to invoke convertToResponse: " + e.getMessage());
            return null;
        }
    }

    private TriggeredRuleDto invokeConvertToTriggeredRuleDto(TriggeredRule rule) {
        try {
            java.lang.reflect.Method method = FraudDetectionService.class.getDeclaredMethod("convertToTriggeredRuleDto", TriggeredRule.class);
            method.setAccessible(true);
            return (TriggeredRuleDto) method.invoke(fraudDetectionService, rule);
        } catch (Exception e) {
            fail("Failed to invoke convertToTriggeredRuleDto: " + e.getMessage());
            return null;
        }
    }
}