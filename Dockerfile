# ========== STAGE 1: BUILDER ==========
# Use Maven with JDK to build the application
# According to java version used
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .

# Install dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package  -DskipTests -DskipITs

# ========== STAGE 2: PRODUCTION ==========
# Use minimal JRE (not JDK) to run the application

FROM eclipse-temurin:21-jre-alpine AS production

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S springgroup && \
    adduser -S springuser -u 1001 -G springgroup

# Copy the built JAR from builder stage
COPY --from=builder --chown=springuser:springgroup /app/target/*.jar app.jar

# Switch to non-root user
USER springuser

# Expose port
EXPOSE 9090

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]