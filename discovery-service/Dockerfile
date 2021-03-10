FROM openjdk:11-jdk
COPY build/libs/*.jar app.jar
EXPOSE 8761

ENV JAVA_OPTS=''
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]

