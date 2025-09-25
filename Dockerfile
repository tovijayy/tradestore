# ---- Build stage ----
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Use Gradle wrapper, not system gradle
RUN ./gradlew clean bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
