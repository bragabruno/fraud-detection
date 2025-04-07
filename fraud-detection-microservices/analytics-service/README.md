# Analytics Service

## Overview
The Analytics Service provides reporting and analytics on fraud detection in the fraud detection microservices architecture. It generates fraud detection reports, provides dashboards for fraud analysts, calculates fraud detection metrics, supports data export, and implements data aggregation.

## Responsibilities
- Generate fraud detection reports
- Provide dashboards for fraud analysts
- Calculate fraud detection metrics
- Support data export
- Implement data aggregation

## Technologies
- Spring Boot
- ClickHouse (column-oriented for analytical queries)
- Spring Cloud for service discovery and configuration
- Kafka for event consumption
- Apache Spark for data processing (optional)
- Grafana for visualization

## API Endpoints

### Analytics
- `GET /api/analytics/metrics`: Get fraud detection metrics
- `GET /api/analytics/reports`: Get fraud detection reports
- `POST /api/analytics/reports`: Generate a custom report
- `GET /api/analytics/export`: Export data in various formats

## Database Schema
The service uses ClickHouse with the following main tables:

```sql
CREATE TABLE fraud_metrics (
  date Date,
  hour UInt8,
  transaction_count UInt32,
  fraud_count UInt32,
  false_positive_count UInt32,
  average_risk_score Float32,
  rule_id String,
  rule_name String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (date, hour, rule_id);

CREATE TABLE transaction_analytics (
  transaction_id String,
  account_id String,
  amount Float64,
  currency String,
  merchant_category String,
  transaction_type String,
  timestamp DateTime,
  risk_score Float32,
  risk_level String,
  is_fraud UInt8,
  triggered_rules Array(String)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (timestamp, account_id);
```

## Event Handling
The service consumes the following events from Kafka:
- `transaction_created`: When a new transaction is created
- `decision_created`: When a new decision is created
- `rule_evaluation_completed`: When rule evaluation is completed

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8086)
- `spring.application.name`: The name of the service (default: analytics-service)
- `spring.clickhouse.*`: ClickHouse connection properties
- `spring.kafka.*`: Kafka connection properties

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- ClickHouse

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
docker build -t analytics-service .
docker run -p 8086:8086 analytics-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
