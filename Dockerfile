FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/simple-chat-user.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]