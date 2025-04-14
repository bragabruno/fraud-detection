#!/bin/bash
set -e

echo "Building application..."

# Load environment-specific variables
source jenkins/environments/${ENVIRONMENT}.env

# Build all microservices
cd fraud-detection-microservices
./gradlew clean build -x test

# Run additional build steps if needed
if [ -f "jenkins/environments/${ENVIRONMENT}.build" ]; then
    source "jenkins/environments/${ENVIRONMENT}.build"
fi

echo "Build completed successfully"