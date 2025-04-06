#!/bin/bash

Change password for
braga.bruno81@gmail.com
Learn more about choosing a smart password

echo "=== Fraud Detection System - Clean Build Script ==="
echo "This script will clean all Gradle caches and perform a fresh build"
echo ""

echo "Step 1: Cleaning Gradle caches..."
# Remove Gradle build cache
rm -rf ~/.gradle/caches/build-cache-*
rm -rf ~/.gradle/caches/modules-2/metadata-*
rm -rf ~/.gradle/caches/modules-2/files-*

echo "Step 2: Cleaning project build directories..."
# Remove project build directories
find . -type d -name "build" -exec rm -rf {} +
find . -type d -name ".gradle" -exec rm -rf {} +

echo "Step 3: Checking Plaid dependency availability..."
curl -s "https://repo1.maven.org/maven2/com/plaid/plaid-java/16.6.0/" > /dev/null
if [ $? -ne 0 ]; then
  echo "Warning: Plaid version 16.6.0 may not be available in Maven Central"
  echo "Trying to find the latest available version..."
  
  # Find available versions
  VERSIONS=$(curl -s "https://repo1.maven.org/maven2/com/plaid/plaid-java/" | grep -o 'href="[0-9][^"]*/"' | cut -d'"' -f2 | tr -d '/')
  LATEST=$(echo "$VERSIONS" | sort -V | tail -n1)
  
  if [ -n "$LATEST" ]; then
    echo "Latest available version appears to be: $LATEST"
    echo "Updating plaid-integration/build.gradle.kts to use this version..."
    sed -i '' "s/plaid-java:[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*/plaid-java:$LATEST/g" plaid-integration/build.gradle.kts
  else
    echo "Could not determine latest version. Will proceed with current configuration."
  fi
fi

echo "Step 4: Running Gradle build with dependency refresh..."
# Run the Gradle clean build command with dependency refresh
./gradlew clean build --refresh-dependencies --stacktrace

echo "Step 5: Verifying Plaid integration dependencies..."
./gradlew :plaid-integration:dependencies | grep -A 5 -B 5 plaid

echo "=== Build process complete ==="
