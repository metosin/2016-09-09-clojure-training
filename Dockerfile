FROM java:8

WORKDIR /training

ADD target/app.jar app.jar

EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
