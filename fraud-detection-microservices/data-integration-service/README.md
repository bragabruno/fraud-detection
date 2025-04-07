# Data Integration Service

## Overview
The Data Integration Service is responsible for connecting to external data sources and transforming external data to internal format in the fraud detection microservices architecture. It manages API keys and credentials, handles rate limiting for external APIs, and provides data synchronization.

## Responsibilities
- Connect to external data sources (Plaid, etc.)
- Transform external data to internal format
- Manage API keys and credentials
- Handle rate limiting for external APIs
- Provide data synchronization

## Technologies
- Spring Boot
- Spring Data MongoDB
- MongoDB (for flexible schema for various data sources)
- Spring Cloud for service discovery and configuration
- Kafka for event publishing
- Spring Integration for external system integration

## API Endpoints

### Integration Management
- `GET /api/integrations`: Get all configured integrations
- `POST /api/integrations`: Configure a new integration
- `GET /api/integrations/{id}`: Get integration by ID
- `PUT /api/integrations/{id}`: Update an integration
- `DELETE /api/integrations/{id}`: Delete an integration

### Data Retrieval
- `GET /api/integrations/{id}/data`: Get data from a specific integration
- `POST /api/integrations/{id}/sync`: Trigger data synchronization

## Database Schema
The service uses MongoDB with the following main collections:

```javascript
// Integration Configuration
{
  "integrations": {
    "id": "String",
    "name": "String",
    "type": "String", // e.g., "PLAID", "EXPERIAN", etc.
    "config": {
      "apiKey": "String",
      "apiSecret": "String",
      "baseUrl": "String",
      "rateLimit": "Number",
      "additionalConfig": "Object"
    },
    "status": "String",
    "created_at": "Date",
    "updated_at": "Date"
  }
}

// External Data
{
  "external_data": {
    "id": "String",
    "integration_id": "String",
    "external_id": "String",
    "data": "Object",
    "processed": "Boolean",
    "created_at": "Date",
    "updated_at": "Date"
  }
}
```

## Event Publishing
The service publishes the following events to Kafka:
- `external_data_received`: When new data is received from an external source
- `integration_status_changed`: When an integration's status changes

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8084)
- `spring.application.name`: The name of the service (default: data-integration-service)
- `spring.data.mongodb.*`: MongoDB connection properties
- `spring.kafka.*`: Kafka connection properties
- `integration.plaid.*`: Plaid API configuration

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
docker build -t data-integration-service .
docker run -p 8084:8084 data-integration-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
