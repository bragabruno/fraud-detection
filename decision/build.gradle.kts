plugins {
    id("buildlogic.java-library-conventions")
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Spring
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.cache)
    
    // Caffeine cache
    implementation(libs.caffeine)
    
    // Workflow engine
    implementation(libs.flowable.spring.boot)
    
    // Jackson for JSON
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)
    
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Testing
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}