FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.14.1_1

WORKDIR /usr/src/app
COPY ./target/*.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","./app.jar", "--port=80"]
