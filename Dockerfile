# syntax=docker/dockerfile:1
FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/demoapi-0.0.1-SNAPSHOT.jar
WORKDIR /dockerapps/demoapi
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]