FROM openjdk:8-jdk-alpine
MAINTAINER zhengtianqi <270490096@qq.com>
VOLUME /tmp
ADD target/springcloud-ztq.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
# DOCKER_HOST=tcp://92.168.2.195:2375 mvn clean package dockerfile:build
# docker run -d -p 8080:8080 -i 825d7f3967cf