FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test --no-daemon

# Extract the JAR file
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Copy the extracted application
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set the entrypoint
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.bragdev.fraud.transaction.TransactionServiceApplication"]

# Expose the application port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1
