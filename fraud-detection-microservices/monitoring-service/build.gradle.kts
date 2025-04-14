plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":shared-libraries"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Monitoring and Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.prometheus:simpleclient_spring_boot")
    implementation("io.prometheus:simpleclient_hotspot")

    // Tracing
    implementation("io.opentracing.contrib:opentracing-spring-jaeger-web-starter")
    
    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}