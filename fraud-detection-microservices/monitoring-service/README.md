# Monitoring Service

## Overview
The Monitoring Service is responsible for system health monitoring and metrics collection in the fraud detection microservices architecture. It monitors system health, collects performance metrics, generates alerts for system issues, provides logging infrastructure, and supports distributed tracing.

## Responsibilities
- Monitor system health
- Collect performance metrics
- Generate alerts for system issues
- Provide logging infrastructure
- Support distributed tracing

## Technologies
- Spring Boot
- Prometheus for metrics collection
- Grafana for visualization
- Elasticsearch, Logstash, Kibana (ELK Stack) for logging
- Jaeger or Zipkin for distributed tracing
- Spring Cloud for service discovery

## API Endpoints

### Monitoring
- `GET /api/health`: Get system health status
- `GET /api/metrics`: Get system metrics
- `GET /api/alerts`: Get active alerts

### Configuration
- `GET /api/monitoring/config`: Get monitoring configuration
- `PUT /api/monitoring/config`: Update monitoring configuration

## Database Schema
The service uses Prometheus and InfluxDB for time-series data:

```
# Prometheus Metrics
- system_cpu_usage
- system_memory_usage
- http_request_duration_seconds
- http_requests_total
- service_up

# InfluxDB Schema
measurement: service_health
fields:
  - status: string
  - response_time: float
  - error_count: integer
tags:
  - service: string
  - instance: string
  - environment: string
```

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8089)
- `spring.application.name`: The name of the service (default: monitoring-service)
- `management.metrics.export.prometheus.*`: Prometheus configuration
- `spring.elasticsearch.*`: Elasticsearch configuration

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- Prometheus
- InfluxDB

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
docker build -t monitoring-service .
docker run -p 8089:8089 monitoring-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```

## Monitoring Stack
The monitoring service is part of a larger monitoring stack that includes:

- Prometheus for metrics collection
- Grafana for visualization
- Elasticsearch for log storage
- Logstash for log processing
- Kibana for log visualization
- Jaeger for distributed tracing

These components are deployed separately and configured to work together.
