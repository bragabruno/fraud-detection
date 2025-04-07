# Configuration Service

## Overview
The Configuration Service provides centralized configuration management for all microservices in the fraud detection architecture. It centralizes configuration management, provides dynamic configuration updates, manages environment-specific configurations, supports feature flags, and handles configuration versioning.

## Responsibilities
- Centralize configuration management
- Provide dynamic configuration updates
- Manage environment-specific configurations
- Support feature flags
- Handle configuration versioning

## Technologies
- Spring Boot
- Spring Cloud Config Server
- Redis (for fast access to configuration data)
- Spring Cloud for service discovery
- Git backend for configuration storage (optional)

## API Endpoints

### Configuration Management
- `GET /api/configs`: Get all configurations
- `GET /api/configs/{name}`: Get configuration by name
- `POST /api/configs`: Create a new configuration
- `PUT /api/configs/{name}`: Update a configuration
- `DELETE /api/configs/{name}`: Delete a configuration

### Feature Flags
- `GET /api/feature-flags`: Get all feature flags
- `GET /api/feature-flags/{name}`: Get feature flag by name
- `POST /api/feature-flags`: Create a new feature flag
- `PUT /api/feature-flags/{name}`: Update a feature flag

## Database Schema
The service uses Redis for fast access to configuration data:

```
# Redis Key-Value Structure

# Configuration
config:{name}:{environment} -> JSON configuration value

# Feature Flags
feature-flag:{name}:{environment} -> Boolean value

# Configuration Metadata
config-metadata:{name} -> JSON metadata (version, last updated, etc.)
```

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8088)
- `spring.application.name`: The name of the service (default: configuration-service)
- `spring.redis.*`: Redis connection properties
- `spring.cloud.config.*`: Spring Cloud Config properties

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- Redis

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
docker build -t configuration-service .
docker run -p 8088:8088 configuration-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
