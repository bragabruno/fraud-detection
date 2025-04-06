plugins {
    id("buildlogic.java-library-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // Project dependencies
    implementation(project(":core"))
    
    // Spring dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Plaid Java client
    implementation("com.plaid:plaid-java:16.6.0")
    
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    
    // Lombok for reducing boilerplate
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}