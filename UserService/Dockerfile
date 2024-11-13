FROM gradle:8-jdk21 AS builder

WORKDIR /home/gradle/scr

COPY --chown=gradle:gradle . /home/gradle/scr

RUN gradle build -x test

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=builder /home/gradle/scr/build/libs/*.jar /app/userservice.jar


EXPOSE 8089

ENTRYPOINT ["java", "-jar", "/app/userservice.jar"]
