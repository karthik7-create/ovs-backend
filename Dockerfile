# ───────────────────────────────────────────────────────────
# Stage 1: Build the application
# ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and config first (better Docker cache)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src/ src/

# Build the JAR (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests -B

# ───────────────────────────────────────────────────────────
# Stage 2: Run the application (slim image)
# ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port (Render uses PORT env variable)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
