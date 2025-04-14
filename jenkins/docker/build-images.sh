#!/bin/bash
set -e

VERSION=$1

if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version>"
    exit 1
fi

# Load environment variables
source jenkins/environments/${ENVIRONMENT}.env

echo "Building Docker images version ${VERSION}..."

# Build base image with shared dependencies
docker build \
    --build-arg JAVA_VERSION=17 \
    --build-arg APP_VERSION=${VERSION} \
    -t ${DOCKER_REGISTRY}/${DOCKER_REPO}/base:${VERSION} \
    -f jenkins/docker/base/Dockerfile .

# Services to build
SERVICES=(
    "api-gateway"
    "transaction"
    "monitoring"
    "notification"
    "rule-engine"
    "analytics"
)

for SERVICE in "${SERVICES[@]}"; do
    echo "Building ${SERVICE} service..."
    
    # Build service image
    docker build \
        --build-arg BASE_IMAGE=${DOCKER_REGISTRY}/${DOCKER_REPO}/base:${VERSION} \
        --build-arg SERVICE_NAME=${SERVICE} \
        --build-arg APP_VERSION=${VERSION} \
        --build-arg JAVA_OPTS="${JAVA_OPTS}" \
        -t ${DOCKER_REGISTRY}/${DOCKER_REPO}/${SERVICE}:${VERSION} \
        -f jenkins/docker/${SERVICE}/Dockerfile \
        fraud-detection-microservices/${SERVICE}-service

    echo "${SERVICE} image built successfully"
done

echo "All Docker images built successfully"