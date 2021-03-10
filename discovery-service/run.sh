#! /bin/sh
./gradlew bootJar
docker build -t cloud-play-discovery-service .
