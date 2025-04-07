# Shared Libraries

## Overview
This repository contains shared libraries and common code used across the fraud detection microservices architecture. It provides reusable components, utilities, and domain models to ensure consistency and reduce duplication across services.

## Contents

### Domain Models
- Common domain entities (Transaction, RiskScore, etc.)
- Data transfer objects (DTOs)
- Value objects

### API Contracts
- Shared API definitions
- Request/response models

### Security Utilities
- Authentication and authorization utilities
- Security helpers

### Common Utilities
- Logging framework
- Exception handling
- Validation utilities
- Date/time utilities

## Usage
This library is included as a dependency in all microservices. To use it in a service, add the following to your build.gradle.kts file:

```kotlin
dependencies {
    implementation(project(":shared-libraries"))
}
```

## Development

### Building
```bash
./gradlew :shared-libraries:build
```

### Testing
```bash
./gradlew :shared-libraries:test
```
