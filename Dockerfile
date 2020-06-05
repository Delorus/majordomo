FROM openjdk:11.0.7-jre-slim-buster
COPY bin/ /apt/majordomo
WORKDIR /apt/majordomo
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "majordomo.jar"]
