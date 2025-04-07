# Fraud Detection Microservices

## Overview
This repository contains the microservices architecture for the fraud detection system. The system is designed to detect fraudulent transactions using a combination of rule-based detection, machine learning, and external data sources.

## Architecture
The fraud detection system is built using a microservices architecture, with the following services:

1. **API Gateway Service**: Entry point for all client requests, routing, and basic validation
2. **Transaction Service**: Manages transaction data and provides transaction-related operations
3. **Rule Engine Service**: Executes fraud detection rules on transactions
4. **Decision Service**: Combines scores from different detection methods
5. **Data Integration Service**: Handles integration with external data sources (including Plaid)
6. **Notification Service**: Manages alerts and notifications for detected fraud
7. **Analytics Service**: Provides reporting and analytics on fraud detection
8. **User Management Service**: Manages user accounts and authentication
9. **Configuration Service**: Centralized configuration management
10. **Monitoring Service**: System health monitoring and metrics

## Shared Libraries
The `shared-libraries` module contains common code used across multiple microservices, including:
- Domain models
- API contracts
- Security utilities
- Common utilities

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.1.x
- **Build Tool**: Gradle
- **Service Discovery**: Consul
- **API Gateway**: Spring Cloud Gateway
- **Messaging**: Kafka
- **Databases**: PostgreSQL, MongoDB, Redis
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack

## Getting Started

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- Docker and Docker Compose
- Kubernetes (optional for local development)

### Building the Project
```bash
./gradlew build
```

### Running Locally with Docker Compose
```bash
docker-compose up -d
```

### Deploying to Kubernetes
```bash
# Apply all Kubernetes manifests
kubectl apply -f kubernetes/
```

## Development Workflow

### Adding a New Microservice
1. Create a new directory for the microservice
2. Add the microservice to `settings.gradle.kts`
3. Implement the microservice using the standard project structure
4. Add Dockerfile and Kubernetes deployment files

### Making Changes to Shared Libraries
1. Make changes to the shared libraries module
2. Run `./gradlew :shared-libraries:build` to build the shared libraries
3. Run `./gradlew build` to rebuild all services that depend on the shared libraries

## Service Interaction
Services communicate with each other through:
1. **Synchronous Communication**: REST APIs for request-response interactions
2. **Asynchronous Communication**: Kafka for event-driven communication

## Monitoring and Observability
- Each service exposes metrics via Spring Boot Actuator
- Prometheus scrapes these metrics for monitoring
- Grafana dashboards visualize the metrics
- Distributed tracing is implemented using Jaeger

## Security
- OAuth 2.0 / OpenID Connect for authentication
- Role-based access control (RBAC)
- API keys for service-to-service communication
- Encryption at rest and in transit

## Contributing
1. Create a feature branch from `main`
2. Make your changes
3. Submit a pull request

## License
This project is licensed under the MIT License - see the LICENSE file for details.
