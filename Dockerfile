FROM maven:3.8.5-amazoncorretto-17 AS build

WORKDIR /app

COPY src src
COPY pom.xml .

RUN mvn clean package
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY --from=build /app/target/api*.jar app.jar
EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]