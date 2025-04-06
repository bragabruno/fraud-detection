// Root build.gradle.kts

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
    // Common configurations for all subprojects can go here
}