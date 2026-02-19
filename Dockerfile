FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY healthcare-bbc/backend/pom.xml healthcare-bbc/backend/pom.xml
COPY healthcare-bbc/backend/src healthcare-bbc/backend/src

WORKDIR /app/healthcare-bbc/backend
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/healthcare-bbc/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
