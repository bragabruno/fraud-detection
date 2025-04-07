plugins {
    id("buildlogic.java-library-conventions")
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    
    // Machine Learning Libraries
    implementation(libs.tensorflow.core.platform)
    implementation(libs.deeplearning4j.core)
    implementation(libs.nd4j.native.platform)
    
    // Statistics and Math
    implementation(libs.commons.math3)
    implementation(libs.smile.core)
    
    // Graph Database
    implementation(libs.neo4j.ogm.core)
    implementation(libs.neo4j.ogm.bolt.driver)
    
    // Spring
    implementation(libs.spring.boot.starter)
    
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