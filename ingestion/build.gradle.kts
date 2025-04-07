plugins {
    id("buildlogic.java-library-conventions")
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Kafka for messaging
    implementation(libs.kafka.clients)
    implementation(libs.kafka.streams)
    
    // Stream processing
    implementation(libs.flink.streaming.java)
    implementation(libs.flink.clients)
    
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Spring
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.kafka)
    
    // Testing
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.kafka.test)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}