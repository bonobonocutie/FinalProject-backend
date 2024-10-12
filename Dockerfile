FROM maven:3.8.6-openjdk-11 AS build

COPY pom.xml /app/
COPY src /app/src/

WORKDIR /app

RUN mvn clean package -DskipTests

FROM openjdk:11-jdk

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]