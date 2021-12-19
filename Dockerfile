FROM openjdk:8-jdk-alpine

RUN mkdir -p /opt/helper
WORKDIR /opt/helper
COPY ./target/github-helper*.jar /opt/helper/github-helper.jar

ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8
ENV TZ Europe/Moscow

ENTRYPOINT ["java", "-Xmx1g", "-Dfile.encoding=UTF-8", "-jar", "/opt/helper/github-helper.jar"]
