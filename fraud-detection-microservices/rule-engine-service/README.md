# Rule Engine Service

## Overview
The Rule Engine Service is responsible for executing fraud detection rules on transactions in the fraud detection microservices architecture. It manages rule configurations, executes rules against transaction data, and provides rule evaluation results.

## Responsibilities
- Execute fraud detection rules
- Manage rule configurations
- Scale rule execution horizontally
- Provide rule evaluation results
- Support different types of rules (geographic, amount-based, etc.)

## Technologies
- Spring Boot
- Spring Data MongoDB
- MongoDB (for flexible schema for different rule types)
- Spring Cloud for service discovery and configuration
- Kafka for event consumption and publishing

## API Endpoints

### Rule Management
- `GET /api/rules`: Get all rules
- `POST /api/rules`: Create a new rule
- `GET /api/rules/{id}`: Get rule by ID
- `PUT /api/rules/{id}`: Update a rule
- `DELETE /api/rules/{id}`: Delete a rule

### Rule Execution
- `POST /api/evaluate`: Evaluate transaction against rules

## Database Schema
The service uses MongoDB with the following main collection:

```javascript
// MongoDB Schema (represented in JSON)
{
  "rules": {
    "id": "String",
    "name": "String",
    "description": "String",
    "category": "String",
    "severity": "Number",
    "parameters": "Object",
    "implementation": "String",
    "active": "Boolean",
    "created_at": "Date",
    "updated_at": "Date"
  }
}
```

## Event Handling
The service consumes the following events from Kafka:
- `transaction_created`: When a new transaction is created

The service publishes the following events to Kafka:
- `rule_evaluation_completed`: When rule evaluation is completed for a transaction

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8082)
- `spring.application.name`: The name of the service (default: rule-engine-service)
- `spring.data.mongodb.*`: MongoDB connection properties
- `spring.kafka.*`: Kafka connection properties

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- MongoDB

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
docker build -t rule-engine-service .
docker run -p 8082:8082 rule-engine-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
