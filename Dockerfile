FROM gradle:7.4.0-jdk8 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build

FROM adoptopenjdk/openjdk11:alpine-slim
ENV PORT=5000
COPY --from=build /app/build/libs/Spring-boot-starter-archetype-1.0.0-SNAPSHOT.jar /app.jar
EXPOSE $PORT
CMD ["java", "-jar", "/app.jar"]