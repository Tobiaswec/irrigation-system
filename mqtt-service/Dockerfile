FROM gradle:7.4.2-jdk17-alpine AS TEMP_BUILD_IMAGE
COPY . /home/source/java
WORKDIR /home/source/java
USER root
RUN chown -R gradle /home/source/java
USER gradle
RUN gradle clean build

FROM openjdk:17
WORKDIR /home/application/java
ARG JAR_FILE=/home/source/java/build/libs/*.jar
COPY --from=TEMP_BUILD_IMAGE  ${JAR_FILE} app.jar
ENTRYPOINT  java -Dspring.profiles.active=dev -jar app.jar

EXPOSE 8080