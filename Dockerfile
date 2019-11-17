FROM openjdk:11-jre-slim
COPY bin/ /apt/majordomo
WORKDIR /apt/majordomo
CMD ["java", "-jar", "majordomo.jar"]