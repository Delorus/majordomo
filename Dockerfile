FROM openjdk:11-jre-slim

COPY ./app/build/libs/ /apt/majordomo

WORKDIR /apt/majordomo

CMD ["java", "-jar", "app-all.jar"]