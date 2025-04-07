plugins {
    id("buildlogic.java-library-conventions")
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(project(":decision"))
    
    // Spring Boot Web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    
    // API Documentation
    implementation(libs.springdoc.openapi)
    
    // Security
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Testing
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}