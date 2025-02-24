FROM openjdk:17

RUN microdnf install -y postgresql && \
    microdnf clean all

WORKDIR /app
COPY target/microservices-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
