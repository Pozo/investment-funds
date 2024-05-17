FROM amazoncorretto:17-alpine-jdk

WORKDIR /app
COPY target/grabber*executable-jar.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]