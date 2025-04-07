# User Management Service

## Overview
The User Management Service is responsible for managing user accounts and authentication in the fraud detection microservices architecture. It handles authentication and authorization, manages user roles and permissions, stores user preferences, and supports user profile management.

## Responsibilities
- Manage user accounts
- Handle authentication and authorization
- Manage user roles and permissions
- Store user preferences
- Support user profile management

## Technologies
- Spring Boot
- Spring Security with OAuth2/OpenID Connect
- Spring Data JPA
- PostgreSQL (for relational data for user accounts)
- Spring Cloud for service discovery and configuration

## API Endpoints

### User Management
- `POST /api/users`: Create a new user
- `GET /api/users/{id}`: Get user by ID
- `PUT /api/users/{id}`: Update a user
- `DELETE /api/users/{id}`: Delete a user
- `GET /api/users`: Get users with filtering

### Authentication
- `POST /api/auth/login`: Authenticate a user
- `POST /api/auth/logout`: Log out a user
- `POST /api/auth/refresh`: Refresh an access token

### Role Management
- `GET /api/roles`: Get all roles
- `POST /api/roles`: Create a new role
- `GET /api/roles/{id}`: Get role by ID
- `PUT /api/roles/{id}`: Update a role
- `DELETE /api/roles/{id}`: Delete a role

## Database Schema
The service uses PostgreSQL with the following main tables:

```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  enabled BOOLEAN DEFAULT TRUE,
  account_non_expired BOOLEAN DEFAULT TRUE,
  account_non_locked BOOLEAN DEFAULT TRUE,
  credentials_non_expired BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE roles (
  id UUID PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
  user_id UUID NOT NULL,
  role_id UUID NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE permissions (
  id UUID PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE role_permissions (
  role_id UUID NOT NULL,
  permission_id UUID NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES roles(id),
  FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
```

## Configuration
The service can be configured through the following properties in `application.properties` or environment variables:

- `server.port`: The port the service runs on (default: 8087)
- `spring.application.name`: The name of the service (default: user-management-service)
- `spring.datasource.*`: Database connection properties
- `spring.security.oauth2.*`: OAuth2 configuration

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
docker build -t user-management-service .
docker run -p 8087:8087 user-management-service
```

## Deployment
The service can be deployed to Kubernetes using the provided deployment files in the `kubernetes` directory.

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```
