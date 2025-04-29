FROM gradle:8.13.0-jdk21 AS builder
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY . .
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G addgroup
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown -R appuser:appgroup /app
ENV SPRING_PROFILE_ACTIVE=production
HEALTHCHECK --interval=5m --timeout=5s \
  CMD ["curl", "-f", "http://localhost:8080/welcome"]
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
