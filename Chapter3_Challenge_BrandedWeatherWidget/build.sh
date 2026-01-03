#!/bin/bash

echo "Building EcoLife Weather Widget..."

# Clean previous builds
mvn clean

# Compile
echo "Compiling..."
mvn compile

# Run tests (if any)
echo "Running tests..."
mvn test

# Create executable JAR
echo "Creating JAR..."
mvn package

# Create distribution folder
mkdir -p dist
cp target/*.jar dist/
cp -r screenshots/ dist/
cp README.md dist/
cp INSTALLATION.md dist/

echo "Build complete! Files are in the 'dist' folder."
echo "To run: java -jar dist/BrandedWeatherWidget-1.0.0.jar"