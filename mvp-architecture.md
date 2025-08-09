# Fraud Detection Engine MVP Architecture

## System Overview

This document outlines a simplified MVP architecture for a Fraud Detection Engine built with Java + Spring Boot, focusing on essential components for transaction processing, rule-based detection, and alert management.

## 1. High-Level Architecture

```mermaid
flowchart TD
    subgraph "External Systems"
        Client[Client Applications]
        Analyst[Fraud Analyst]
    end

    subgraph "API Layer"
        API[Spring Boot REST API]
        Auth[Authentication Service]
    end

    subgraph "Core Services"
        TxService[Transaction Service]
        RuleEngine[Rule Engine]
        RiskScoring[Risk Scoring Service]
        AlertService[Alert Management Service]
    end

    subgraph "Data Layer"
        DB[(PostgreSQL Database)]
        RuleConfig[Rule Configuration]
    end

    subgraph "Supporting Services"
        Validation[Input Validation]
        Logging[Audit Logging]
        Health[Health Monitoring]
    end

    Client --> API
    Analyst --> API
    API --> Auth
    API --> Validation
    API --> TxService

    TxService --> RuleEngine
    RuleEngine --> RuleConfig
    RuleEngine --> RiskScoring
    RiskScoring --> AlertService

    TxService --> DB
    AlertService --> DB
    RuleEngine --> Logging

    API --> Health

    classDef external fill:#e1f5fe
    classDef api fill:#f3e5f5
    classDef core fill:#e8f5e8
    classDef data fill:#fff3e0
    classDef support fill:#fce4ec

    class Client,Analyst external
    class API,Auth api
    class TxService,RuleEngine,RiskScoring,AlertService core
    class DB,RuleConfig data
    class Validation,Logging,Health support
```

## 2. Data Flow Architecture

```mermaid
sequenceDiagram
    participant C as Client
    participant API as REST API
    participant V as Validation
    participant TS as Transaction Service
    participant RE as Rule Engine
    participant RS as Risk Scoring
    participant AS as Alert Service
    participant DB as Database
    participant L as Audit Logger

    C->>API: POST /transactions
    API->>V: Validate Input
    V-->>API: Validation Result

    alt Valid Transaction
        API->>TS: Process Transaction
        TS->>DB: Store Transaction
        TS->>RE: Evaluate Rules

        RE->>RE: Apply Business Rules
        RE->>RS: Calculate Risk Score
        RS-->>RE: Risk Score & Level

        alt High/Medium Risk
            RE->>AS: Create Alert
            AS->>DB: Store Alert
            AS-->>API: Alert Created
        else Low Risk
            RE-->>API: Transaction Approved
        end

        RE->>L: Log Decision
        API-->>C: Response (Status + Alert ID if any)

    else Invalid Transaction
        API-->>C: Validation Error
    end
```

## 3. Component Interaction Details

```mermaid
graph TB
    subgraph "Request Processing Flow"
        A[Incoming Transaction Request]
        B{Input Validation}
        C[Transaction Service]
        D[Rule Engine]
        E[Risk Scoring Service]
        F[Alert Service]
        G{Risk Level Decision}
        H[Database Persistence]
        I[Audit Logging]
        J[API Response]
    end

    subgraph "Rule Processing"
        D1[Amount Rule]
        D2[Velocity Rule]
        D3[Location Rule]
        D4[Time Pattern Rule]
        D5[Rule Aggregator]
    end

    subgraph "Data Storage"
        H1[(Transactions Table)]
        H2[(Alerts Table)]
        H3[(Rules Table)]
        H4[(Audit Log Table)]
    end

    A --> B
    B -->|Valid| C
    B -->|Invalid| J
    C --> H1
    C --> D

    D --> D1
    D --> D2
    D --> D3
    D --> D4
    D1 --> D5
    D2 --> D5
    D3 --> D5
    D4 --> D5

    D5 --> E
    E --> G
    G -->|High/Medium Risk| F
    G -->|Low Risk| J
    F --> H2
    F --> J

    D --> I
    E --> I
    F --> I
    I --> H4

    classDef process fill:#e3f2fd
    classDef decision fill:#fff3e0
    classDef storage fill:#f1f8e9
    classDef rule fill:#fce4ec

    class A,C,D,E,F,I process
    class B,G decision
    class H1,H2,H3,H4 storage
    class D1,D2,D3,D4,D5 rule
```

## 4. Component Details

### 3.1 API Layer Components

#### REST API Controller
- **Endpoint**: `/transactions` (POST)
- **Endpoint**: `/alerts` (GET)
- **Endpoint**: `/alerts/{id}` (PATCH)
- **Endpoint**: `/rules` (GET, POST, PUT - Optional)
- **Features**: Request/Response validation, Error handling, API documentation

#### Authentication Service
- **Type**: API Key based authentication (Spring Security)
- **Features**: Request authentication, Rate limiting, Access control

### 3.2 Core Service Components

#### Transaction Service
- **Responsibilities**:
  - Transaction persistence
  - Data enrichment
  - Batch processing coordination
- **Features**: Transaction validation, Duplicate detection, Audit trail

#### Rule Engine
- **Rule Types**:
  - Amount threshold rules
  - Velocity rules (transaction frequency)
  - Geographic anomaly rules
  - Time-based pattern rules
- **Features**: Config-driven rules, Rule evaluation pipeline, Rule performance metrics

#### Risk Scoring Service
- **Scoring Method**: Weighted sum of triggered rules
- **Risk Levels**: Low (0-30), Medium (31-70), High (71-100)
- **Features**: Configurable weights, Score explanation, Historical scoring

#### Alert Management Service
- **Alert Lifecycle**: OPEN → REVIEWED → RESOLVED
- **Features**: Alert prioritization, Status updates, Alert queries with filters

### 3.3 Data Layer

#### Database Schema (PostgreSQL)

```sql
-- Transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    merchant_id VARCHAR(50),
    merchant_category VARCHAR(20),
    location VARCHAR(100),
    timestamp TIMESTAMP NOT NULL,
    channel VARCHAR(20),
    device_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rules table
CREATE TABLE rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rule_type VARCHAR(50) NOT NULL,
    parameters JSONB NOT NULL,
    weight INTEGER DEFAULT 10,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Alerts table
CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    transaction_id UUID REFERENCES transactions(id),
    risk_score INTEGER NOT NULL,
    risk_level VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'OPEN',
    triggered_rules JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    resolved_at TIMESTAMP,
    reviewer_id VARCHAR(50),
    resolution_notes TEXT
);

-- Audit log table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    transaction_id UUID,
    action VARCHAR(50) NOT NULL,
    details JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 4. API Endpoints Specification

### Transaction Processing
```http
POST /api/v1/transactions
Content-Type: application/json

{
  "accountId": "ACC123456",
  "amount": 1500.00,
  "currency": "USD",
  "merchantId": "MERCH789",
  "merchantCategory": "GROCERY",
  "location": "New York, NY",
  "timestamp": "2024-01-15T10:30:00Z",
  "channel": "ONLINE",
  "deviceId": "DEVICE123"
}
```

**Response:**
```json
{
  "transactionId": "tx-uuid-123",
  "status": "PROCESSED",
  "riskLevel": "MEDIUM",
  "alertId": "alert-uuid-456",
  "riskScore": 65,
  "triggeredRules": ["HIGH_AMOUNT", "UNUSUAL_LOCATION"]
}
```

### Alert Management
```http
GET /api/v1/alerts?status=OPEN&riskLevel=HIGH&page=0&size=20

PATCH /api/v1/alerts/{alertId}
{
  "status": "REVIEWED",
  "reviewerNotes": "Legitimate transaction confirmed with customer"
}
```

## 5. Technology Stack

### Core Framework
- **Java 17+**: Base language
- **Spring Boot 3.2+**: Application framework
- **Spring Data JPA**: Data access layer
- **Spring Security**: Authentication and authorization
- **Spring Boot Actuator**: Health monitoring and metrics

### Database
- **PostgreSQL**: Primary database (or H2 for development)
- **HikariCP**: Connection pooling
- **Flyway**: Database migrations

### Build and Deployment
- **Gradle**: Build tool
- **Docker**: Containerization
- **Docker Compose**: Local development environment

### Supporting Libraries
- **Lombok**: Reduce boilerplate code
- **Jackson**: JSON processing
- **SLF4J + Logback**: Logging
- **JUnit 5**: Testing framework
- **Testcontainers**: Integration testing

## 6. Configuration Examples

### Rule Configuration (YAML)
```yaml
rules:
  - name: "HIGH_AMOUNT"
    type: "AMOUNT_THRESHOLD"
    parameters:
      threshold: 1000.00
      currency: "USD"
    weight: 30
    active: true

  - name: "VELOCITY_CHECK"
    type: "TRANSACTION_FREQUENCY"
    parameters:
      maxTransactions: 5
      timeWindowMinutes: 60
    weight: 25
    active: true

  - name: "UNUSUAL_LOCATION"
    type: "GEOGRAPHIC_ANOMALY"
    parameters:
      maxDistanceKm: 100
      timeWindowHours: 2
    weight: 20
    active: true
```

## 7. Deployment Architecture

```mermaid
flowchart TB
    subgraph "Container Environment"
        subgraph "Application Layer"
            App1[Fraud Detection App Instance 1]
            App2[Fraud Detection App Instance 2]
        end

        subgraph "Database Layer"
            DB[(PostgreSQL)]
            DBBackup[(Backup Storage)]
        end

        subgraph "Infrastructure"
            LB[Load Balancer]
            Monitor[Monitoring Stack]
            Logs[Log Aggregation]
        end
    end

    LB --> App1
    LB --> App2
    App1 --> DB
    App2 --> DB
    DB --> DBBackup

    App1 --> Logs
    App2 --> Logs
    Monitor --> App1
    Monitor --> App2
    Monitor --> DB
```

## 8. Spring Boot Project Structure

```
fraud-detection-mvp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/bragdev/frauddetection/
│   │   │       ├── FraudDetectionApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── TransactionController.java
│   │   │       │   └── AlertController.java
│   │   │       ├── service/
│   │   │       │   ├── TransactionService.java
│   │   │       │   ├── RuleEngineService.java
│   │   │       │   ├── RiskScoringService.java
│   │   │       │   └── AlertService.java
│   │   │       ├── repository/
│   │   │       │   ├── TransactionRepository.java
│   │   │       │   ├── AlertRepository.java
│   │   │       │   └── RuleRepository.java
│   │   │       ├── model/
│   │   │       │   ├── entity/
│   │   │       │   │   ├── Transaction.java
│   │   │       │   │   ├── Alert.java
│   │   │       │   │   └── Rule.java
│   │   │       │   └── dto/
│   │   │       │       ├── TransactionRequest.java
│   │   │       │       ├── TransactionResponse.java
│   │   │       │       └── AlertResponse.java
│   │   │       ├── engine/
│   │   │       │   ├── rules/
│   │   │       │   │   ├── Rule.java
│   │   │       │   │   ├── AmountRule.java
│   │   │       │   │   ├── VelocityRule.java
│   │   │       │   │   └── LocationRule.java
│   │   │       │   ├── RuleEvaluator.java
│   │   │       │   └── RiskCalculator.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── DatabaseConfig.java
│   │   │       │   └── RuleConfig.java
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── FraudDetectionException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           ├── V1__Create_transactions_table.sql
│   │           ├── V2__Create_rules_table.sql
│   │           ├── V3__Create_alerts_table.sql
│   │           └── V4__Create_audit_logs_table.sql
│   └── test/
│       └── java/
│           └── com/bragdev/frauddetection/
│               ├── controller/
│               ├── service/
│               ├── engine/
│               └── integration/
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── docker-compose.dev.yml
├── gradle/
├── build.gradle.kts
└── README.md
```

## 9. Implementation Roadmap

### Phase 1: Core MVP (Weeks 1-2)
**Deliverable**: Basic fraud detection system with simple rules

**Tasks:**
- [ ] Set up Spring Boot project with multi-module structure
- [ ] Configure PostgreSQL database and JPA entities
- [ ] Implement Transaction entity and repository
- [ ] Create basic REST controller for transaction processing
- [ ] Implement 3 basic rules (Amount, Velocity, Location)
- [ ] Create simple rule engine and risk scoring
- [ ] Basic alert creation and storage
- [ ] Unit tests for core components

**Key Components to Build:**
- [`TransactionController.java`](src/main/java/com/bragdev/frauddetection/controller/TransactionController.java)
- [`TransactionService.java`](src/main/java/com/bragdev/frauddetection/service/TransactionService.java)
- [`RuleEngineService.java`](src/main/java/com/bragdev/frauddetection/service/RuleEngineService.java)
- [`AmountRule.java`](src/main/java/com/bragdev/frauddetection/engine/rules/AmountRule.java)
- Database migration scripts

### Phase 2: Enhanced Detection (Weeks 3-4)
**Deliverable**: Production-ready rule engine with comprehensive API

**Tasks:**
- [ ] Add more sophisticated rules (Time patterns, Merchant category)
- [ ] Implement weighted risk scoring system
- [ ] Add alert management endpoints (GET, PATCH)
- [ ] Implement batch transaction processing capability
- [ ] Add comprehensive input validation and error handling
- [ ] Create rule configuration management
- [ ] Integration tests for all APIs

**Key Components to Build:**
- [`AlertController.java`](src/main/java/com/bragdev/frauddetection/controller/AlertController.java)
- [`AlertService.java`](src/main/java/com/bragdev/frauddetection/service/AlertService.java)
- [`RiskScoringService.java`](src/main/java/com/bragdev/frauddetection/service/RiskScoringService.java)
- Additional rule implementations
- Validation and exception handling

### Phase 3: Production Ready (Weeks 5-6)
**Deliverable**: Secure, monitored, containerized application

**Tasks:**
- [ ] Implement API key authentication with Spring Security
- [ ] Add comprehensive audit logging
- [ ] Integrate Spring Boot Actuator for health monitoring
- [ ] Create Docker containerization setup
- [ ] Add performance monitoring and metrics
- [ ] Load testing and performance optimization
- [ ] Documentation and deployment guides

**Key Components to Build:**
- [`SecurityConfig.java`](src/main/java/com/bragdev/frauddetection/config/SecurityConfig.java)
- [`GlobalExceptionHandler.java`](src/main/java/com/bragdev/frauddetection/exception/GlobalExceptionHandler.java)
- Docker configuration files
- Monitoring and metrics setup

### Phase 4: Advanced Features (Future Enhancements)
**Deliverable**: Enhanced system with management capabilities

**Future Tasks:**
- [ ] Build rule management UI for business users
- [ ] Add advanced analytics dashboard
- [ ] Integrate machine learning models for pattern detection
- [ ] External system integrations (Core Banking, KYC)
- [ ] Advanced reporting and compliance features
- [ ] A/B testing framework for rule effectiveness

## 9. Key Design Decisions

1. **Database Choice**: PostgreSQL for ACID compliance and JSON support for flexible rule parameters
2. **Rule Storage**: Database-stored rules with JSON parameters for flexibility
3. **API Design**: RESTful APIs with clear separation of concerns
4. **Security**: API key authentication for MVP, with OAuth2 as future enhancement
5. **Deployment**: Docker containers for consistency and scalability
6. **Monitoring**: Spring Actuator for basic health checks, with external monitoring integration points

This MVP architecture provides a solid foundation for a fraud detection system while maintaining simplicity and allowing for future enhancements.
