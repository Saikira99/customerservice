# -----------------------------------------
# Stage 1: Build the application with Maven
# -----------------------------------------
FROM openjdk:21-jdk-slim AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# -----------------------------------------
# Stage 2: Create the runtime image
# -----------------------------------------
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/customerservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
