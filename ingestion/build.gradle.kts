plugins {
    id("buildlogic.java-library-conventions")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Kafka for messaging
    implementation("org.apache.kafka:kafka-clients:3.6.0")
    implementation("org.apache.kafka:kafka-streams:3.6.0")
    
    // Stream processing
    implementation("org.apache.flink:flink-streaming-java:1.17.0")
    implementation("org.apache.flink:flink-clients:1.17.0")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // Spring
    implementation("org.springframework.boot:spring-boot-starter:3.2.4")
    implementation("org.springframework.kafka:spring-kafka:3.1.0")
    
    // Testing
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.0")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}