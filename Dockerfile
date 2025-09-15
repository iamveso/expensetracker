# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app

# Copy Maven wrapper and config first (better caching)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=builder /app/target/expensetracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
