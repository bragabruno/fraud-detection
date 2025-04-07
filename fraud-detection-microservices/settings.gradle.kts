rootProject.name = "fraud-detection-microservices"

// Include all microservices
include(
    "shared-libraries",
    "api-gateway-service",
    "transaction-service",
    "rule-engine-service",
    "decision-service",
    "data-integration-service",
    "notification-service",
    "analytics-service",
    "user-management-service",
    "configuration-service",
    "monitoring-service"
)
