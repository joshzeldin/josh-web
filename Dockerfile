FROM openjdk:8-alpine

COPY target/uberjar/josh-web.jar /josh-web/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/josh-web/app.jar"]
