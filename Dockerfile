FROM maven:3-jdk-11
EXPOSE 80
ADD target/loterias-api.jar loterias-api.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "/loterias-api.jar", "--server.port=80"]