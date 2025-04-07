# Notification Service

## Overview
The Notification Service is responsible for sending alerts for high-risk transactions in the fraud detection microservices architecture. It manages notification preferences, supports multiple notification channels (email, SMS, push), handles notification delivery status, and implements notification throttling.

## Responsibilities
- Send alerts for high-risk transactions
- Manage notification preferences
- Support multiple notification channels (email, SMS, push)
- Handle notification delivery status
- Implement notification throttling

## Technologies
- Spring Boot
- Spring Data JPA
- PostgreSQL (for persistence)
- Redis (for fast in-memory processing)
- Spring Cloud for service discovery and configuration
- Kafka for event consumption
- JavaMail for email notifications
- Twilio for SMS notifications

## API Endpoints

### Notification Management
- `POST /api/notifications`: Send a notification
- `GET /api/notifications/{id}`: Get notification by ID
- `GET /api/notifications`: Get notifications with filtering

### Preference Management
- `GET /api/preferences/{userId}`: Get notification preferences for a user
- `PUT /api/preferences/{userId}`: Update notification preferences for a user

## Database Schema
The service uses PostgreSQL with the following main tables:

```sql
CREATE TABLE notification_preferences (
  id UUID PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  email_enabled BOOLEAN DEFAULT TRUE,
  sms_enabled BOOLEAN DEFAULT FALSE,
  push_enabled BOOLEAN DEFAULT FALSE,
  email_address VARCHAR(255),
  phone_number VARCHAR(20),
  device_token TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  notification_type VARCHAR(50) NOT NULL,
  channel VARCHAR(20) NOT NULL,
  subject VARCHAR(255),
  content TEXT NOT NULL,
  status VARCHAR(20) NOT NULL,
  sent_at TIMESTAMP,
  delivered_at TIMESTAMP,
  error_message TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

## Event Handling
The service consumes the following events from Kafka:
- `decision_created`: When a new decision is created with high risk

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8085)
- `spring.application.name`: The name of the service (default: notification-service)
- `spring.datasource.*`: Database connection properties
- `spring.redis.*`: Redis connection properties
- `spring.kafka.*`: Kafka connection properties
- `notification.email.*`: Email notification configuration
- `notification.sms.*`: SMS notification configuration

## Building and Running

### Prerequisites
- JDK 17 or later
- Gradle 7.0 or later
- PostgreSQL
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
docker build -t notification-service .
docker run -p 8085:8085 notification-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
