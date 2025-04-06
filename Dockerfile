FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Gradle files for dependency caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc

# Run Gradle to cache dependencies
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY . .

# Build the application
RUN ./gradlew --no-daemon clean build -x test

# Run the application
ENTRYPOINT ["java", "-jar", "app/build/libs/app.jar"]

# JVM tuning for containerized environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1