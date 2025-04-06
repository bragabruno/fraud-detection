# Fraud Detection System: Architecture Overview

## High-Level Architecture

```mermaid
graph TD
    subgraph "API Layer"
        A[FraudDetectionController] --> B[FraudDetectionService]
        B --> C1[DTO Conversion]
        B --> C2[Transaction Evaluation]
        B --> C3[Batch Processing]
    end
    
    subgraph "Detection Engine"
        D[SimpleRuleEngine] --> E1[Rule 1: GeographicAnomalyRule]
        D --> E2[Rule 2: VelocityCheckRule]
        D --> E3[Rule 3: UnusualMerchantCategoryRule]
        D --> E4[Rule 4: TimeBasedPatternRule]
        D --> E5[Rule 5: HighValueTransactionRule]
    end
    
    subgraph "Decision Layer"
        F[DecisionManager] --> G1[Rule Engine Score]
        F --> G2[Statistical Score]
        F --> G3[Machine Learning Score]
        F --> G4[Network Analysis Score]
    end
    
    subgraph "Core Domain Model"
        H1[Transaction]
        H2[RiskScore]
        H3[RiskLevel]
        H4[TriggeredRule]
    end
    
    B --> D
    B --> F
    D --> H2
    F --> H2
    E1 --> H4
    E2 --> H4
    E3 --> H4
    E4 --> H4
    E5 --> H4
    H4 --> H2
```

## Data Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller as FraudDetectionController
    participant Service as FraudDetectionService
    participant Engine as RuleEngine
    participant Rules as Detection Rules
    participant DecMgr as DecisionManager
    
    Client->>Controller: POST /evaluate (Transaction)
    Controller->>Service: evaluateTransaction(TransactionDto)
    Service->>Service: Convert DTO to Domain Model
    Service->>Engine: evaluate(Transaction)
    Engine->>Rules: Apply all rules
    Rules-->>Engine: Return triggered rules
    Engine-->>Service: Return RiskScore
    Service->>DecMgr: makeDecision(Transaction, scores)
    DecMgr-->>Service: Return final RiskScore
    Service->>Service: Convert to Response DTO
    Service-->>Controller: Return FraudDetectionResponse
    Controller-->>Client: Return HTTP Response
```

## Component Relationships

```mermaid
classDiagram
    class FraudDetectionController {
        +evaluateTransaction()
        +evaluateBatch()
    }
    
    class FraudDetectionService {
        -ruleEngine: RuleEngine
        -decisionManager: DecisionManager
        +evaluateTransaction()
        +evaluateBatch()
        -convertToTransaction()
        -convertToResponse()
    }
    
    class RuleEngine {
        +addRule()
        +evaluate()
    }
    
    class Rule {
        <<interface>>
        +evaluate()
        +createTriggeredRule()
        +generateTriggerReason()
    }
    
    class BaseRule {
        <<abstract>>
        -id: String
        -name: String
        -description: String
        -category: String
        -severity: double
        +evaluate()
    }
    
    class DecisionManager {
        -methodWeights: Map
        +makeDecision()
        +setMethodWeight()
    }
    
    class Transaction {
        -id: UUID
        -amount: BigDecimal
        -timestamp: Instant
        -location: GeoLocation
        ...
    }
    
    class RiskScore {
        -id: UUID
        -transactionId: UUID
        -overallScore: double
        -riskLevel: RiskLevel
        -triggeredRules: List
        ...
    }
    
    FraudDetectionController --> FraudDetectionService
    FraudDetectionService --> RuleEngine
    FraudDetectionService --> DecisionManager
    RuleEngine --> Rule
    BaseRule --|> Rule
    GeographicAnomalyRule --|> BaseRule
    VelocityCheckRule --|> BaseRule
    UnusualMerchantCategoryRule --|> BaseRule
    TimeBasedPatternRule --|> Rule
    Rule --> Transaction
    Rule --> RiskScore
```

## Package Structure

```
com.bragdev.fraud
├── api
│   ├── controller
│   │   └── FraudDetectionController
│   ├── dto
│   │   ├── DeviceInfoDto
│   │   ├── FraudDetectionRequest
│   │   ├── FraudDetectionResponse
│   │   ├── GeoLocationDto
│   │   ├── TransactionDto
│   │   ├── TransactionRiskDto
│   │   └── TriggeredRuleDto
│   └── service
│       └── FraudDetectionService
├── core
│   ├── engine
│   │   ├── RuleEngine (interface)
│   │   └── SimpleRuleEngine (implementation)
│   ├── model
│   │   ├── DeviceInfo
│   │   ├── GeoLocation
│   │   ├── RiskLevel (enum)
│   │   ├── RiskScore
│   │   ├── Transaction
│   │   ├── TransactionType (enum)
│   │   └── TriggeredRule
│   └── rule
│       ├── Rule (interface)
│       └── BaseRule (abstract)
├── decision
│   └── DecisionManager
└── detection
    ├── demo
    │   └── RuleEngineDemo
    └── rule
        ├── GeographicAnomalyRule
        ├── HighValueTransactionRule
        ├── TimeBasedPatternRule
        ├── UnusualMerchantCategoryRule
        └── VelocityCheckRule
```

## Current Issues and Fix Approach

```mermaid
graph TD
    subgraph "Current Issues"
        I1[Missing API Components] --> F1[Implement Missing DTOs]
        I1 --> F2[Implement Service Layer]
        
        I2[Dependency Conflicts] --> F3[Standardize Dependencies]
        I2 --> F4[Create Version Management]
        
        I3[Implementation Inconsistencies] --> F5[Standardize Rule Implementations]
        I3 --> F6[Fix Thread Safety]
        
        I4[Build Failures] --> F7[Fix Lombok Configuration]
        I4 --> F8[Update Gradle Configuration]
    end
    
    subgraph "Expected Results"
        F1 --> R1[Complete API Layer]
        F2 --> R1
        
        F3 --> R2[Consistent Dependency Management]
        F4 --> R2
        
        F5 --> R3[Consistent Code Architecture]
        F6 --> R3
        
        F7 --> R4[Successful Build Process]
        F8 --> R4
    end
```

## Detailed Documentation Index

We have created the following comprehensive documentation to address the fraud detection system issues:

1. **[Troubleshooting Plan](troubleshooting-plan.md)**: Technical analysis and solution approach
2. **[Implementation Steps](implementation-steps.md)**: Code-level implementation details
3. **[Dependency Analysis](dependency-analysis.md)**: In-depth dependency issue resolution
4. **[Testing Strategy](testing-strategy.md)**: Comprehensive testing approach
5. **[Execution Plan](execution-plan.md)**: Step-by-step implementation plan

## Implementation Timeline

```mermaid
gantt
    title Fraud Detection System Fix Implementation
    dateFormat  YYYY-MM-DD
    section Preparation
    Create Workspace Branch :a1, 2025-04-07, 1d
    Backup Current State    :a2, after a1, 1d
    section Core Fixes
    Fix Missing Components   :b1, 2025-04-07, 1d
    Fix Dependency Issues    :b2, 2025-04-08, 1d
    Fix Implementation Issues:b3, 2025-04-08, 1d
    section Validation
    Unit Testing            :c1, 2025-04-09, 1d
    Integration Testing     :c2, after c1, 1d
    Performance Optimization:c3, 2025-04-10, 1d
    section Finalization
    Documentation Update    :d1, 2025-04-10, 1d
    Final Validation        :d2, after d1, 1d
    Pull Request & Review   :d3, after d2, 1d