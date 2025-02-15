# -----------------------------------------
# Stage 1: Build the application with Maven
# -----------------------------------------
FROM maven:3.9.1-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the pom.xml first (to cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the entire source code
COPY src ./src

# Build the application (skip tests if desired)
RUN mvn clean package -DskipTests

# -----------------------------------------
# Stage 2: Create a lightweight runtime image
# -----------------------------------------
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/customerservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (must match your application.properties)
EXPOSE 8081

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
