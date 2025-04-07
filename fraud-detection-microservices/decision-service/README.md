# Decision Service

## Overview
The Decision Service is responsible for combining scores from different fraud detection methods and determining the final risk level for transactions in the fraud detection microservices architecture. It applies weighting to different detection methods, provides decision explanations, and supports decision overrides.

## Responsibilities
- Combine scores from different detection methods
- Apply weighting to different detection methods
- Determine final risk level
- Provide decision explanations
- Support decision overrides

## Technologies
- Spring Boot
- Spring Data JPA
- PostgreSQL (for transactional integrity for decisions)
- Spring Cloud for service discovery and configuration
- Kafka for event consumption and publishing

## API Endpoints

### Decision Management
- `POST /api/decisions`: Create a new decision
- `GET /api/decisions/{id}`: Get decision by ID
- `GET /api/decisions`: Get decisions with filtering
- `POST /api/decisions/{id}/override`: Override a decision

## Database Schema
The service uses PostgreSQL with the following main tables:

```sql
CREATE TABLE decisions (
  id UUID PRIMARY KEY,
  transaction_id UUID NOT NULL,
  risk_score DECIMAL(5, 2) NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  decision_status VARCHAR(20) NOT NULL,
  explanation TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE decision_factors (
  id UUID PRIMARY KEY,
  decision_id UUID NOT NULL,
  factor_name VARCHAR(100) NOT NULL,
  factor_score DECIMAL(5, 2) NOT NULL,
  factor_weight DECIMAL(5, 2) NOT NULL,
  factor_explanation TEXT,
  created_at TIMESTAMP NOT NULL,
  FOREIGN KEY (decision_id) REFERENCES decisions(id)
);

CREATE TABLE decision_overrides (
  id UUID PRIMARY KEY,
  decision_id UUID NOT NULL,
  original_risk_level VARCHAR(20) NOT NULL,
  new_risk_level VARCHAR(20) NOT NULL,
  override_reason TEXT NOT NULL,
  overridden_by VARCHAR(100) NOT NULL,
  overridden_at TIMESTAMP NOT NULL,
  FOREIGN KEY (decision_id) REFERENCES decisions(id)
);
```

## Event Handling
The service consumes the following events from Kafka:
- `rule_evaluation_completed`: When rule evaluation is completed for a transaction

The service publishes the following events to Kafka:
- `decision_created`: When a new decision is created
- `decision_updated`: When a decision is updated or overridden

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8083)
- `spring.application.name`: The name of the service (default: decision-service)
- `spring.datasource.*`: Database connection properties
- `spring.kafka.*`: Kafka connection properties

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- PostgreSQL

### Building
```bash
./gradlew build
```

### Running Locally
```bash
./gradlew bootRun
```

### Running with Docker
```bash
docker build -t decision-service .
docker run -p 8083:8083 decision-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
