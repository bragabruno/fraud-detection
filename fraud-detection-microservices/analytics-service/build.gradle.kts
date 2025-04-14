plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.micrometer.prometheus")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":shared-libraries"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    
    // Metrics and Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.opentracing.contrib:opentracing-spring-jaeger-web-starter")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.mockito:mockito-core")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}