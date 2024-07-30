# PatJudge Dockerfile

FROM openjdk:17-jdk-slim

ARG VERSION
ARG PROFILE

COPY target/PatJudge-${VERSION}.jar /application.jar

EXPOSE 8081

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "/application.jar", "--spring.profiles.active=${PROFILE}"]
