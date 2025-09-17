FROM openjdk:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar file from target folder into container
COPY target/EmployeeManagementSystem-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 1000

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]