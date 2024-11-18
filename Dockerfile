# Etap 1: Budowanie aplikacji Spring Boot
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# Etap 2: Uruchomienie aplikacji na serwerze Tomcat
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]


##Stage 1
## initialize build and set base image for first stage
#FROM maven:3.8.3-openjdk-17 AS stage1
## speed up Maven JVM a bit
#ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
## set working directory
#WORKDIR /opt/demo
## copy just pom.xml
#COPY pom.xml .
## go-offline using the pom.xml
#RUN mvn dependency:go-offline
## copy your other files
#COPY ./src ./src
## compile the source code and package it in a jar file
#RUN mvn clean install -Dmaven.test.skip=true
##Stage 2
## set base image for second stage
#FROM openjdk:17-jdk-slim
## set deployment directory
#WORKDIR /opt/demo
## copy over the built artifact from the maven image
#COPY --from=stage1 /opt/demo/target/demo.jar /opt/demo