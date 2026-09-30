# Use an ARM base image that already includes OpenCV and Java for ARM64
FROM balenalib/raspberrypi4-64-openjdk:17-bullseye-build

# Set the working directory
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Make gradlew executable
RUN chmod +x ./gradlew

# Run Gradle build (use --no-daemon to avoid daemon issues in Docker)
RUN ./gradlew build --no-daemon

# Command to run the Java application
CMD ["./gradlew", "run"]