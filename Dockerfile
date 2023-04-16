FROM gradle:7.4-jdk8 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

FROM openjdk:8
COPY --from=build /app/build/libs/Spring-boot-starter-archetype-1.0.0-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
