#! /bin/sh
./gradlew bootJar
docker build -t cloud-play-router-service .
