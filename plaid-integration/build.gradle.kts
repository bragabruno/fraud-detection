plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    // Project dependencies
    implementation(project(":core"))
    
    // Spring dependencies
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    
    // Plaid Java client
    implementation(libs.plaid.java)
    
    // Jackson for JSON processing
    implementation(libs.jackson.databind)
    
    // Lombok for reducing boilerplate
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Testing dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)
}