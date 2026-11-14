FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/indexing-worker-0.0.1-SNAPSHOT.jar indexing-worker.jar
ENTRYPOINT ["java", "-jar", "indexing-worker.jar"]
