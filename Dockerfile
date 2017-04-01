FROM openjdk:8-jdk-alpine

ADD build/libs/docker-mapped-file.jar /usr/bin/app/

CMD ["java", "-jar", "/usr/bin/app/docker-mapped-file.jar"]
