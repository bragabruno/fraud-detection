# Transaction Service

## Overview
The Transaction Service is responsible for managing transaction data in the fraud detection microservices architecture. It provides APIs for storing, retrieving, and validating transaction data, as well as transaction history and lifecycle management.

## Responsibilities
- Store and retrieve transaction data
- Validate transaction data
- Provide transaction history
- Manage transaction metadata
- Handle transaction lifecycle

## Technologies
- Spring Boot
- Spring Data JPA
- PostgreSQL (for ACID compliance with financial data)
- Spring Cloud for service discovery and configuration
- Kafka for event publishing

## API Endpoints

### Transaction Management
- `POST /api/transactions`: Create a new transaction
- `GET /api/transactions/{id}`: Get transaction by ID
- `GET /api/transactions`: Get transactions with filtering
- `PUT /api/transactions/{id}/status`: Update transaction status

## Database Schema
The service uses PostgreSQL with the following main table:

```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  account_id VARCHAR(255) NOT NULL,
  amount DECIMAL(19, 4) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  merchant_id VARCHAR(255),
  merchant_name VARCHAR(255),
  merchant_category VARCHAR(255),
  timestamp TIMESTAMP NOT NULL,
  channel VARCHAR(50),
  transaction_type VARCHAR(50) NOT NULL,
  received_at TIMESTAMP NOT NULL,
  transaction_reference VARCHAR(255),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

## Event Publishing
The service publishes the following events to Kafka:
- `transaction_created`: When a new transaction is created
- `transaction_updated`: When a transaction is updated

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8081)
- `spring.application.name`: The name of the service (default: transaction-service)
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
docker build -t transaction-service .
docker run -p 8081:8081 transaction-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
