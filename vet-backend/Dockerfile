# --- build ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# --- execução ---
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --create-home vet && mkdir -p /app/data && chown vet /app/data
COPY --from=build /app/target/*.jar app.jar
USER vet
EXPOSE 8080
# PORT é definida pela hospedagem; o Spring lê server.port=${PORT:8080}
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
