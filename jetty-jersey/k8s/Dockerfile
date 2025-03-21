# syntax=docker/dockerfile:1
#FROM eclipse-temurin:17-jdk-jammy
FROM eclipse-temurin:17-jre
#FROM openjdk:11-jre-slim
# copy the packaged jar file into our docker image
COPY io.onme.stuck-jetty-jersey-0.1.0-SNAPSHOT-jar-with-dependencies.jar /stuck.jar
# COPY stuck.properties /stuck.properties
# set the startup command to execute the jar
CMD ["java", "-jar", "/stuck.jar","-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager"]