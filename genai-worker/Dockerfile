FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/genai-worker-0.0.1-SNAPSHOT.jar genai-worker.jar
ENTRYPOINT ["java", "-jar", "genai-worker.jar"]
