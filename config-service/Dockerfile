FROM openjdk:11-jdk
COPY build/libs/*.jar app.jar
COPY config config
EXPOSE 8888

ENV JAVA_OPTS=''
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -DCONFIG_LOC=/config -jar /app.jar"]

