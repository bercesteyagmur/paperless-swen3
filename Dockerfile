# This is a "multi-stage build": two separate environments in one file.
# Stage 1 compiles the code
# Stage 2 only runs the already-compiled jar.

# Stage 1: build
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# We copy the wrapper + pom.xml first, before the source code, and run
# dependency download here as its own step.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

#  copy the actual source code and compile the jar
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: run
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the built jar from stage 1 - no source code
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]