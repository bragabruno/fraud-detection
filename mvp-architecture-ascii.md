# Fraud Detection Engine MVP Architecture (ASCII Version)

## System Overview

This document outlines a simplified MVP architecture for a Fraud Detection Engine built with Java + Spring Boot, focusing on essential components for transaction processing, rule-based detection, and alert management.

## 1. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           EXTERNAL SYSTEMS                                 │
├─────────────────────────────┬───────────────────────────────────────────────┤
│     Client Applications     │           Fraud Analyst                      │
└─────────────┬───────────────┴─────────────┬─────────────────────────────────┘
              │                             │
              ▼                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API LAYER                                     │
├─────────────────────────────┬───────────────────────────────────────────────┤
│  Spring Boot REST API       │      Authentication Service                  │
└─────────────┬───────────────┴─────────────┬─────────────────────────────────┘
              │                             │
              ▼                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CORE SERVICES                                   │
├──────────────┬──────────────┬─────────────┬────────────────────────────────┤
│ Transaction  │ Rule Engine  │ Risk Scoring│    Alert Management            │
│   Service    │              │   Service   │       Service                  │
└──────┬───────┴──────┬───────┴─────────┬───┴──────┬─────────────────────────┘
       │              │                 │          │
       ▼              ▼                 ▼          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                             DATA LAYER                                     │
├─────────────────────────────┬───────────────────────────────────────────────┤
│    PostgreSQL Database      │         Rule Configuration                   │
└─────────────────────────────┴───────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          SUPPORTING SERVICES                               │
├──────────────────┬──────────────────────┬───────────────────────────────────┤
│  Input Validation│   Audit Logging      │     Health Monitoring             │
└──────────────────┴──────────────────────┴───────────────────────────────────┘
```

## 2. Data Flow Architecture

```
Client Request: POST /transactions
        │
        ▼
┌─────────────────┐
│ Input Validation│
└─────┬───────────┘
      │ Valid?
      ▼
   ┌─Yes─┐    ┌─No──┐
   │     │    │     │
   ▼     │    ▼     │
┌────────────┐ ┌─────────────┐
│Transaction │ │Return Error │
│  Service   │ │   Response  │
└─────┬──────┘ └─────────────┘
      │
      ▼
┌────────────────────────────────┐
│     Store in Database          │
└─────┬──────────────────────────┘
      │
      ▼
┌─────────────────┐
│   Rule Engine   │
└─────┬───────────┘
      │
      ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Amount Rule   │    │  Velocity Rule  │    │ Location Rule   │
└─────┬───────────┘    └─────┬───────────┘    └─────┬───────────┘
      │                      │                      │
      └──────────┬───────────────────────────────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Risk Scoring    │
        │   Service       │
        └─────┬───────────┘
              │
              ▼
        ┌─────────────────┐
        │ Risk Decision   │
        └─────┬───────────┘
              │
    ┌─────────┴──────────┐
    │                    │
    ▼                    ▼
┌─High/Medium Risk─┐  ┌─Low Risk─┐
│                  │  │          │
▼                  │  ▼          │
┌─────────────────┐│ ┌─────────────────┐
│ Create Alert    ││ │ Approve Trans.  │
│ Store in DB     ││ │                 │
└─────┬───────────┘│ └─────────────────┘
      │            │
      ▼            │
┌─────────────────┐│
│ Audit Logging   ││
└─────┬───────────┘│
      │            │
      └────────────┘
      │
      ▼
┌─────────────────┐
│ API Response    │
│ (Status + Alert)│
└─────────────────┘
```

## 3. Component Interaction Flow

```
Transaction Processing Pipeline:
═══════════════════════════════

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Transaction   │ => │     Input       │ => │   Transaction   │
│    Request      │    │   Validation    │    │    Service      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Database     │ <= │     Rule        │ <= │   Transaction   │
│   Persistence   │    │    Engine       │    │     Store       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Rules Library  │
                       ├─────────────────┤
                       │ • Amount Rule   │
                       │ • Velocity Rule │
                       │ • Location Rule │
                       │ • Time Rule     │
                       └─────┬───────────┘
                             │
                             ▼
                       ┌─────────────────┐
                       │ Risk Scoring    │
                       │   & Decision    │
                       └─────┬───────────┘
                             │
          ┌──────────────────┴──────────────────┐
          │                                     │
          ▼                                     ▼
    ┌─High/Med Risk─┐                    ┌─Low Risk────┐
    │               │                    │             │
    ▼               │                    ▼             │
┌─────────────────┐ │              ┌─────────────────┐ │
│ Alert Service   │ │              │ Direct Approval │ │
│ • Create Alert  │ │              │                 │ │
│ • Store Alert   │ │              └─────────────────┘ │
│ • Notify Analyst│ │                                  │
└─────┬───────────┘ │                                  │
      │             │                                  │
      └─────────────┴──────────────────────────────────┘
                    │
                    ▼
              ┌─────────────────┐
              │ Audit & Logging │
              └─────┬───────────┘
                    │
                    ▼
              ┌─────────────────┐
              │  API Response   │
              │  to Client      │
              └─────────────────┘
```

## 4. Database Schema (ASCII ERD)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              TRANSACTIONS                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│ id (UUID, PK)                                                              │
│ account_id (VARCHAR)                                                        │
│ amount (DECIMAL)                                                            │
│ currency (VARCHAR)                                                          │
│ merchant_id (VARCHAR)                                                       │
│ merchant_category (VARCHAR)                                                 │
│ location (VARCHAR)                                                          │
│ timestamp (TIMESTAMP)                                                       │
│ channel (VARCHAR)                                                           │
│ device_id (VARCHAR)                                                         │
│ created_at (TIMESTAMP)                                                      │
└─────┬───────────────────────────────────────────────────────────────────────┘
      │
      │ (1:N)
      │
      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                ALERTS                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ id (UUID, PK)                                                              │
│ transaction_id (UUID, FK) ──────────────────────────────────────────────────┘
│ risk_score (INTEGER)
│ risk_level (VARCHAR)
│ status (VARCHAR) -- OPEN, REVIEWED, RESOLVED
│ triggered_rules (JSONB)
│ created_at (TIMESTAMP)
│ reviewed_at (TIMESTAMP)
│ resolved_at (TIMESTAMP)
│ reviewer_id (VARCHAR)
│ resolution_notes (TEXT)
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                RULES                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ id (BIGSERIAL, PK)                                                          │
│ name (VARCHAR)                                                              │
│ description (TEXT)                                                          │
│ rule_type (VARCHAR)                                                         │
│ parameters (JSONB)                                                          │
│ weight (INTEGER)                                                            │
│ active (BOOLEAN)                                                            │
│ created_at (TIMESTAMP)                                                      │
│ updated_at (TIMESTAMP)                                                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              AUDIT_LOGS                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│ id (BIGSERIAL, PK)                                                          │
│ transaction_id (UUID)                                                       │
│ action (VARCHAR)                                                            │
│ details (JSONB)                                                             │
│ timestamp (TIMESTAMP)                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 5. API Endpoints Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          FRAUD DETECTION API                               │
└─────────────────────────────────────────────────────────────────────────────┘

POST /api/v1/transactions
├─ Headers: Authorization, Content-Type: application/json
├─ Request Body:
│  {
│    "accountId": "ACC123456",
│    "amount": 1500.00,
│    "currency": "USD",
│    "merchantId": "MERCH789",
│    "merchantCategory": "GROCERY",
│    "location": "New York, NY",
│    "timestamp": "2024-01-15T10:30:00Z",
│    "channel": "ONLINE",
│    "deviceId": "DEVICE123"
│  }
└─ Response:
   {
     "transactionId": "tx-uuid-123",
     "status": "PROCESSED",
     "riskLevel": "MEDIUM",
     "alertId": "alert-uuid-456",
     "riskScore": 65,
     "triggeredRules": ["HIGH_AMOUNT", "UNUSUAL_LOCATION"]
   }

GET /api/v1/alerts
├─ Query Parameters: status, riskLevel, page, size
└─ Response: Paginated list of alerts

PATCH /api/v1/alerts/{alertId}
├─ Request Body:
│  {
│    "status": "REVIEWED",
│    "reviewerNotes": "Confirmed with customer"
│  }
└─ Response: Updated alert details

GET /api/v1/rules (Optional)
└─ Response: List of configured rules

POST /api/v1/health
└─ Response: System health status
```

## 6. Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        CONTAINER ENVIRONMENT                               │
└─────────────────────────────────────────────────────────────────────────────┘

                         ┌─────────────────┐
                         │  Load Balancer  │
                         └─────┬───────────┘
                               │
            ┌──────────────────┼──────────────────┐
            │                  │                  │
            ▼                  ▼                  ▼
   ┌─────────────────┐┌─────────────────┐┌─────────────────┐
   │ Fraud Detection ││ Fraud Detection ││ Fraud Detection │
   │  App Instance 1 ││  App Instance 2 ││  App Instance N │
   └─────┬───────────┘└─────┬───────────┘└─────┬───────────┘
         │                  │                  │
         └──────────────────┼──────────────────┘
                            │
                            ▼
                  ┌─────────────────┐
                  │   PostgreSQL    │
                  │    Database     │
                  └─────┬───────────┘
                        │
                        ▼
                  ┌─────────────────┐
                  │ Backup Storage  │
                  └─────────────────┘

   ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
   │ Monitoring      │ │ Log Aggregation │ │ Health Checks   │
   │ (Prometheus)    │ │ (ELK Stack)     │ │ (Actuator)      │
   └─────────────────┘ └─────────────────┘ └─────────────────┘
```

## 7. Technology Stack Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           TECHNOLOGY STACK                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  APPLICATION LAYER                                                          │
│  ┌─ Java 17+ ───────────────────────────────────────────────────────────┐   │
│  │ ┌─ Spring Boot 3.2+ ──────────────────────────────────────────────┐ │   │
│  │ │ • Spring Web (REST API)                                         │ │   │
│  │ │ • Spring Data JPA (Database Access)                             │ │   │
│  │ │ • Spring Security (Authentication)                              │ │   │
│  │ │ • Spring Boot Actuator (Health Monitoring)                      │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  DATABASE LAYER                                                             │
│  ┌─ PostgreSQL ─────────────────────────────────────────────────────────┐   │
│  │ • ACID Compliance                                                   │   │
│  │ • JSON Support (JSONB)                                              │   │
│  │ • Connection Pooling (HikariCP)                                     │   │
│  │ • Database Migrations (Flyway)                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  BUILD & DEPLOYMENT                                                         │
│  ┌─ Gradle ─────────────────────────────────────────────────────────────┐   │
│  │ ┌─ Docker ────────────────────────────────────────────────────────┐ │   │
│  │ │ • Application Containerization                                  │ │   │
│  │ │ • Docker Compose (Development)                                  │ │   │
│  │ │ • Multi-stage builds                                            │ │   │
│  │ └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  SUPPORTING LIBRARIES                                                       │
│  • Lombok (Code Generation)                                                │
│  • Jackson (JSON Processing)                                               │
│  • SLF4J + Logback (Logging)                                               │
│  • JUnit 5 (Testing)                                                       │
│  • Testcontainers (Integration Testing)                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 8. Implementation Phases

```
PHASE 1: CORE MVP (Weeks 1-2)
═════════════════════════════════════════════════════════════════════════════
Goal: Basic fraud detection system with simple rules

┌─ Week 1 ─────────────────────┐    ┌─ Week 2 ─────────────────────┐
│ • Project Setup              │    │ • Rule Engine Implementation │
│ • Database Schema            │    │ • Risk Scoring Service       │
│ • Basic REST Controllers     │    │ • Alert Management           │
│ • Transaction Entity/Service │    │ • Unit Tests                 │
└─────────────────────────────┘    └─────────────────────────────┘

Deliverables:
├─ TransactionController.java
├─ TransactionService.java
├─ Basic Rule implementations (Amount, Velocity, Location)
├─ Database migration scripts
└─ Basic API functionality

PHASE 2: ENHANCED DETECTION (Weeks 3-4)
═════════════════════════════════════════════════════════════════════════════
Goal: Production-ready rule engine with comprehensive API

┌─ Week 3 ─────────────────────┐    ┌─ Week 4 ─────────────────────┐
│ • Advanced Rules             │    │ • Alert Management API       │
│ • Weighted Risk Scoring      │    │ • Batch Processing           │
│ • Rule Configuration         │    │ • Integration Tests          │
│ • Input Validation           │    │ • Error Handling             │
└─────────────────────────────┘    └─────────────────────────────┘

Deliverables:
├─ AlertController.java
├─ RiskScoringService.java
├─ Advanced rule implementations
├─ Comprehensive validation
└─ Full API coverage

PHASE 3: PRODUCTION READY (Weeks 5-6)
═════════════════════════════════════════════════════════════════════════════
Goal: Secure, monitored, containerized application

┌─ Week 5 ─────────────────────┐    ┌─ Week 6 ─────────────────────┐
│ • Security Implementation    │    │ • Docker Setup               │
│ • Audit Logging             │    │ • Performance Testing        │
│ • Health Monitoring          │    │ • Documentation              │
│ • Metrics Collection         │    │ • Deployment Guides          │
└─────────────────────────────┘    └─────────────────────────────┘

Deliverables:
├─ SecurityConfig.java
├─ Docker configuration
├─ Monitoring setup
├─ Performance benchmarks
└─ Production deployment guide
```

## 9. Key Architecture Decisions

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ARCHITECTURE DECISIONS                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  DATABASE CHOICE: PostgreSQL                                               │
│  ┌─ Rationale ──────────────────────────────────────────────────────────┐   │
│  │ • ACID compliance for financial data integrity                       │   │
│  │ • JSONB support for flexible rule parameters                         │   │
│  │ • Strong consistency guarantees                                      │   │
│  │ • Excellent performance for OLTP workloads                           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  RULE STORAGE: Database + JSON Parameters                                  │
│  ┌─ Rationale ──────────────────────────────────────────────────────────┐   │
│  │ • Dynamic rule configuration without code changes                    │   │
│  │ • Audit trail for rule modifications                                 │   │
│  │ • Version control for rule sets                                      │   │
│  │ • Easy backup and restore                                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  API DESIGN: RESTful with Clear Resource Separation                        │
│  ┌─ Rationale ──────────────────────────────────────────────────────────┐   │
│  │ • Standard HTTP methods and status codes                             │   │
│  │ • Easy integration with external systems                             │   │
│  │ • Clear separation of concerns                                       │   │
│  │ • Self-documenting endpoints                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  SECURITY: API Key Authentication (MVP)                                    │
│  ┌─ Rationale ──────────────────────────────────────────────────────────┐   │
│  │ • Simple to implement and manage                                     │   │
│  │ • Sufficient for internal system integration                         │   │
│  │ • Easy to extend to OAuth2/JWT later                                 │   │
│  │ • Clear audit trail                                                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  DEPLOYMENT: Docker Containers                                             │
│  ┌─ Rationale ──────────────────────────────────────────────────────────┐   │
│  │ • Consistent environment across dev/test/prod                        │   │
│  │ • Easy scaling and orchestration                                     │   │
│  │ • Simplified dependency management                                   │   │
│  │ • Cloud platform agnostic                                           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

This MVP architecture provides a solid foundation for a fraud detection system while maintaining simplicity and allowing for future enhancements.
