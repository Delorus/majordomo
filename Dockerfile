FROM openjdk:11-jre
COPY bin/ /apt/majordomo
WORKDIR /apt/majordomo
CMD ["java", "-jar", "majordomo.jar"]