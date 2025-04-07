// Root build.gradle.kts

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    
    // Make the Spring Boot plugin available to all projects
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

// Configure all projects in the build
allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

// Configure only the subprojects, not the root project
subprojects {
    // Apply Java plugin to all subprojects
    apply(plugin = "java")
    
    // Common Java configuration
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Common test configuration
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}