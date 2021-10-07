FROM openjdk:8u181-jre
COPY target/task-management-rest-1.0-SNAPSHOT.jar .
COPY config.yml .
ENV server.port 8080
WORKDIR .
ENTRYPOINT ["java" ,"-jar", "task-management-rest-1.0-SNAPSHOT.jar" ,"server" ,"config.yml"]