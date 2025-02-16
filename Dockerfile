# Use OpenJDK 21 for runtime
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/customerservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (informational; the actual port is set by the environment)
EXPOSE 8081

# Run the application (environment variables will be injected by Render or Docker Compose)
ENTRYPOINT ["java", "-jar", "app.jar"]
