FROM openjdk:11-jre-slim
COPY ./telegrambot/build/libs/ /apt/majordomo
WORKDIR /apt/majordomo
CMD ["java", "-jar", "majordomo-all.jar"]