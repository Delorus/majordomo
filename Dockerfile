FROM openjdk:11-jre

EXPOSE 5005
EXPOSE 8080

COPY bin/ /apt/majordomo
WORKDIR /apt/majordomo
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "majordomo.jar"]
