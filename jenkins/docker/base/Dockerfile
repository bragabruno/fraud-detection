FROM eclipse-temurin:17-jdk-alpine as builder

# Build arguments
ARG APP_VERSION
ARG JAVA_VERSION=17

# Set environment variables
ENV APP_VERSION=${APP_VERSION}
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Install build dependencies
RUN apk add --no-cache \
    curl \
    wget \
    tar \
    bash \
    git \
    && rm -rf /var/cache/apk/*

# Create app directory
WORKDIR /app

# Copy gradle files
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy source code
COPY fraud-detection-microservices/shared-libraries shared-libraries

# Build shared libraries
RUN ./gradlew :shared-libraries:build

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set environment variables
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Install runtime dependencies
RUN apk add --no-cache \
    curl \
    bash \
    tzdata \
    && rm -rf /var/cache/apk/*

# Create app user
RUN addgroup -S app && adduser -S app -G app

# Create app directories
RUN mkdir -p /app/config /app/logs \
    && chown -R app:app /app

# Set working directory
WORKDIR /app

# Copy shared libraries from builder
COPY --from=builder --chown=app:app /app/shared-libraries/build/libs /app/libs

# Set user
USER app

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Default JVM options
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication -XX:+ExitOnOutOfMemoryError"

# Expose common ports
EXPOSE 8080

# Default command
CMD ["bash"]