plugins {
    id("buildlogic.java-application-conventions")
}

group = "com.bragdev"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(project(":utilities"))

    // Spring Boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jpa)

    // Lombok
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)

    // Database
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
}

application {
    // Define the main class for the application.
    mainClass = "com.bragdev.fraud.FraudDetectionApplication"
}
