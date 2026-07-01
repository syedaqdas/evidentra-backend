FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /workspace/app
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN useradd --system --uid 10001 --home-dir /app evidentra

COPY --from=build /workspace/app/target/evidentra-backend-*.jar /app/evidentra-backend.jar

USER evidentra
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/evidentra-backend.jar"]
