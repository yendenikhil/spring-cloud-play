#! /bin/sh
./gradlew bootJar
docker build -t cloud-play-config-service .
