plugins {
    id("buildlogic.java-library-conventions")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Machine Learning Libraries
    implementation("org.tensorflow:tensorflow-core-platform:0.5.0")
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M1.1")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-M1.1")
    
    // Statistics and Math
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.github.haifengl:smile-core:2.6.0")
    
    // Graph Database
    implementation("org.neo4j:neo4j-ogm-core:4.0.5")
    implementation("org.neo4j:neo4j-ogm-bolt-driver:4.0.5")
    
    // Spring
    implementation("org.springframework.boot:spring-boot-starter:3.2.4")
    
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