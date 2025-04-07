# API Gateway Service

## Overview
The API Gateway Service serves as the entry point for all client requests in the fraud detection microservices architecture. It handles routing, authentication, authorization, and provides a unified interface for clients to interact with the various microservices.

## Responsibilities
- Route requests to appropriate microservices
- Handle authentication and authorization
- Implement rate limiting and request validation
- Provide API documentation
- Implement circuit breaker patterns

## Technologies
- Spring Cloud Gateway
- Spring Security
- Spring Boot Actuator
- Resilience4j for circuit breaking
- Swagger/OpenAPI for documentation

## API Documentation
API documentation is available at `/swagger-ui.html` when the service is running.

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8080)
- `spring.application.name`: The name of the service (default: api-gateway-service)
- `spring.cloud.gateway.routes`: Gateway routing configuration

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later

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
docker build -t api-gateway-service .
docker run -p 8080:8080 api-gateway-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
