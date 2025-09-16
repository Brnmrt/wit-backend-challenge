FROM eclipse-temurin:21-jdk-jammy
VOLUME /tmp
COPY target/rest-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]


