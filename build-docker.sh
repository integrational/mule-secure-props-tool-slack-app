#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

# native build doesn't work once Slack is added
#./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
#docker build -f src/main/docker/Dockerfile.native-micro -t $IMG .
# normal Java-based Docker image instead
./gradlew build -Dquarkus.package.type=uber-jar
docker build -f src/main/docker/Dockerfile.jvm -t $IMG .
