FROM maven:3.6.0-jdk-8-alpine

RUN mkdir /app
WORKDIR /app
ADD pom.xml pom.xml
ADD src src

RUN mvn verify

EXPOSE 8000
ENTRYPOINT ["java", "-jar", "target/simple-bank-1.0.0-SNAPSHOT-jar-with-dependencies.jar"]