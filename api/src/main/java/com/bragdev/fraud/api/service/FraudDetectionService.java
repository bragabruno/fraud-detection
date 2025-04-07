package com.bragdev.fraud.api.service;

import com.bragdev.fraud.api.dto.*;
import com.bragdev.fraud.core.engine.RuleEngine;
import com.bragdev.fraud.core.model.*;
import com.bragdev.fraud.decision.DecisionManager;
import com.bragdev.fraud.detection.demo.RuleEngineDemo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FraudDetectionService {
    
    private final RuleEngine ruleEngine;
    private final DecisionManager decisionManager;
    
    public FraudDetectionService() {
        // Initialize with standard rules
        this.ruleEngine = RuleEngineDemo.createStandardRuleEngine();
        this.decisionManager = new DecisionManager();
    }
    
    public FraudDetectionResponse evaluateTransaction(TransactionDto transactionDto) {
        // Convert DTO to domain model
        Transaction transaction = convertToTransaction(transactionDto);
        
        // Evaluate with rule engine
        RiskScore ruleEngineScore = ruleEngine.evaluate(transaction);
        
        // Create a map for decision manager
        Map<String, RiskScore> scores = new HashMap<>();
        scores.put("RULE_ENGINE", ruleEngineScore);
        
        // Make final decision
        RiskScore finalScore = decisionManager.makeDecision(transaction, scores);
        
        // Convert to response
        return convertToResponse(finalScore);
    }
    
    public FraudDetectionResponse evaluateBatch(List<TransactionDto> transactionDtos) {
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            throw new IllegalArgumentException("Transaction list cannot be null or empty");
        }
        
        List<TransactionRiskDto> results = new ArrayList<>();
        
        // Process each transaction
        for (TransactionDto dto : transactionDtos) {
            Transaction transaction = convertToTransaction(dto);
            RiskScore score = ruleEngine.evaluate(transaction);
            
            Map<String, RiskScore> scores = new HashMap<>();
            scores.put("RULE_ENGINE", score);
            
            RiskScore finalScore = decisionManager.makeDecision(transaction, scores);
            
            TransactionRiskDto riskDto = new TransactionRiskDto();
            riskDto.setTransactionId(transaction.getId().toString());
            riskDto.setRiskScore(finalScore.getOverallScore());
            riskDto.setRiskLevel(finalScore.getRiskLevel().toString());
            
            results.add(riskDto);
        }
        
        // Create batch response
        FraudDetectionResponse response = new FraudDetectionResponse();
        response.setResults(results);
        return response;
    }
    
    // Helper methods for conversion
    private Transaction convertToTransaction(TransactionDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Transaction DTO cannot be null");
        }
        
        GeoLocation location = null;
        if (dto.getLocation() != null) {
            location = new GeoLocation(
                dto.getLocation().getLatitude(),
                dto.getLocation().getLongitude()
            );
        }
        
        DeviceInfo deviceInfo = null;
        if (dto.getDeviceInfo() != null) {
            deviceInfo = new DeviceInfo(
                dto.getDeviceInfo().getBrowser(),
                dto.getDeviceInfo().getOperatingSystem(),
                dto.getDeviceInfo().getIpAddress()
            );
        }
        
        UUID id = dto.getId() != null ? UUID.fromString(dto.getId()) : UUID.randomUUID();
        
        return Transaction.builder()
                .id(id)
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .merchantId(dto.getMerchantId())
                .merchantName(dto.getMerchantName())
                .merchantCategory(dto.getMerchantCategory())
                .timestamp(dto.getTimestamp())
                .location(location)
                .channel(dto.getChannel())
                .deviceInfo(deviceInfo)
                .type(dto.getType() != null ? TransactionType.valueOf(dto.getType()) : TransactionType.PURCHASE)
                .additionalAttributes(dto.getAdditionalAttributes())
                .receivedAt(Instant.now())
                .transactionReference(id.toString())
                .build();
    }
    
    private FraudDetectionResponse convertToResponse(RiskScore score) {
        if (score == null) {
            throw new IllegalArgumentException("Risk score cannot be null");
        }
        
        FraudDetectionResponse response = new FraudDetectionResponse();
        response.setTransactionId(score.getTransactionId().toString());
        response.setRiskScore(score.getOverallScore());
        response.setRiskLevel(score.getRiskLevel().toString());
        response.setComponentScores(score.getComponentScores());
        response.setExplanations(score.getExplanations());
        
        if (score.getTriggeredRules() != null) {
            List<TriggeredRuleDto> triggeredRules = score.getTriggeredRules().stream()
                .map(this::convertToTriggeredRuleDto)
                .collect(Collectors.toList());
            
            response.setTriggeredRules(triggeredRules);
        }
        
        return response;
    }
    
    private TriggeredRuleDto convertToTriggeredRuleDto(TriggeredRule rule) {
        TriggeredRuleDto dto = new TriggeredRuleDto();
        dto.setRuleId(rule.getRuleId());
        dto.setRuleName(rule.getRuleName());
        dto.setCategory(rule.getCategory());
        dto.setSeverity(rule.getSeverity());
        dto.setTriggerReason(rule.getTriggerReason());
        return dto;
    }
}