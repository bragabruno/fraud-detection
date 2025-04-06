plugins {
    id("buildlogic.java-library-conventions")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Spring
    implementation("org.springframework.boot:spring-boot-starter:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.2.4")
    
    // Caffeine cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    
    // Workflow engine
    implementation("org.flowable:flowable-spring-boot-starter:7.0.0")
    
    // Jackson for JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // Testing
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}