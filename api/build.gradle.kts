plugins {
    id("buildlogic.java-library-conventions")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(project(":decision"))
    
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.4")
    
    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.4")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // Testing
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
    testImplementation("org.springframework.security:spring-security-test:6.2.1")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}