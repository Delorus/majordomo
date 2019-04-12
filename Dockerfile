FROM openjdk:8u212-slim-stretch

COPY ./build/libs/ /apt/majordomo

WORKDIR /apt/majordomo

CMD ["java", "-jar", "majordomo-all.jar"]