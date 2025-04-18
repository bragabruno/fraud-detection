plugins {
    // Support convention plugins written in Kotlin
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal and maven central
    gradlePluginPortal()
    mavenCentral()
}

// Removed version catalog extension to avoid duplicate access

dependencies {
    // Make the Spring Boot plugin available to convention plugins
    // Using direct dependencies instead of version catalog to avoid circular references
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
}
