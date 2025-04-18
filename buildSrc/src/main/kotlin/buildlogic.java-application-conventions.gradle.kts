plugins {
    // Apply the common convention plugin for shared build configuration
    id("buildlogic.java-common-conventions")

    // Apply the application plugin to add support for building a CLI application in Java
    application
    
    // Apply Spring Boot plugin for application modules
    id("org.springframework.boot")
}

// Spring Boot configuration
springBoot {
    buildInfo()
}
