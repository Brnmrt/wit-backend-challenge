FROM eclipse-temurin:21-jdk-jammy
VOLUME /tmp
COPY target/calculator-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]


