FROM gradle:8.13.0-jdk21 AS builder
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY . .
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/build/libs/*.jar app.jar
ENV SPRING_PROFILE_ACTIVE=production
ENTRYPOINT ["java", "-jar", "app.jar"]
