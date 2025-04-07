plugins {
    id("org.springframework.boot") version "3.1.5" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    id("java") apply false
}

allprojects {
    group = "com.bragdev.fraud"
    version = "0.1.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
