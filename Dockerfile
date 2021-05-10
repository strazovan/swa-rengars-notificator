FROM openjdk:16-jdk-alpine
COPY target/notifications-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]