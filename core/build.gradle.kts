plugins {
    id("buildlogic.java-library-conventions")
}

group = "com.bragdev.fraud"
version = "0.0.1-SNAPSHOT"

dependencies {
    // Lombok - correctly configured as compileOnly and annotationProcessor only
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Jakarta Validation
    implementation(libs.jakarta.validation)
    
    // Jackson
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    
    // Testing
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.junit.jupiter)
}

// Make sure annotation processing is enabled
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}