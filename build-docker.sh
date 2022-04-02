#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

# native build doesn't work once Slack is added
#./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
#docker build --pull -f src/main/docker/Dockerfile.native-micro -t $IMG .

# normal Java-based Docker image instead
# build normal jar (not uber-jar) as required by the Docker image
./gradlew clean build -Dquarkus.package.type=jar
docker build --pull -f src/main/docker/Dockerfile.jvm -t $IMG .
