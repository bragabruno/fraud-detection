# Implementation Steps for Fraud Detection System Fixes

## Directory Structure to Create

```
api/src/main/java/com/bragdev/fraud/api/dto/
├── DeviceInfoDto.java
├── FraudDetectionRequest.java
├── FraudDetectionResponse.java
├── GeoLocationDto.java
├── TransactionDto.java
├── TransactionRiskDto.java
└── TriggeredRuleDto.java

api/src/main/java/com/bragdev/fraud/api/service/
└── FraudDetectionService.java
```

## Step 1: Create the DTO Classes

### TransactionDto.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
public class TransactionDto {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private Instant timestamp;
    private GeoLocationDto location;
    private String channel;
    private DeviceInfoDto deviceInfo;
    private String type;
    private Map<String, Object> additionalAttributes;
}
```

### GeoLocationDto.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class GeoLocationDto {
    private double latitude;
    private double longitude;
}
```

### DeviceInfoDto.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class DeviceInfoDto {
    private String browser;
    private String operatingSystem;
    private String ipAddress;
}
```

### FraudDetectionRequest.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class FraudDetectionRequest {
    private TransactionDto transaction;
    private List<TransactionDto> transactions;
}
```

### FraudDetectionResponse.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FraudDetectionResponse {
    private String transactionId;
    private double riskScore;
    private String riskLevel;
    private List<TriggeredRuleDto> triggeredRules;
    private Map<String, Double> componentScores;
    private Map<String, Object> explanations;
    
    // For batch processing
    private List<TransactionRiskDto> results;
}
```

### TriggeredRuleDto.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class TriggeredRuleDto {
    private String ruleId;
    private String ruleName;
    private String category;
    private double severity;
    private String triggerReason;
}
```

### TransactionRiskDto.java
```java
package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class TransactionRiskDto {
    private String transactionId;
    private double riskScore;
    private String riskLevel;
}
```

## Step 2: Create the Service Implementation

### FraudDetectionService.java
```java
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
```

## Step 3: Fix Gradle Dependency Issues

### Update build.gradle.kts to standardize versions

Create a new file called `versions.gradle.kts` in the root project directory:

```kotlin
object Versions {
    // Core dependencies
    const val springBoot = "3.2.4"
    const val springDependencyManagement = "1.1.4"
    const val springdoc = "2.3.0"
    const val jackson = "2.16.0"
    const val junit = "5.11.3"
    const val lombok = "1.18.30"
    const val mockito = "5.10.0"
    
    // Integrations
    const val plaid = "16.6.0"
    const val tensorflow = "0.5.0"
    const val deeplearning4j = "1.0.0-M1.1"
    const val nd4j = "1.0.0-M1.1"
    const val commonsMath = "3.6.1"
    const val smile = "2.6.0"
    const val neo4j = "4.0.5"
    const val caffeine = "3.1.8"
    const val flowable = "7.0.0"
}
```

## Implementation Notes

1. **Directory Creation**: Ensure all required directories exist before creating files
2. **Package Structure**: Follow the existing package structure in the project
3. **Consistent Imports**: Use consistent imports across all files
4. **Testing**: Create unit tests for each new component
5. **Build Validation**: Run a clean build after implementing changes to verify fixes