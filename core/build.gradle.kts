plugins {
    id("buildlogic.java-library-conventions")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.projectlombok:lombok:1.18.30")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
}

// Make sure annotation processing is enabled
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}