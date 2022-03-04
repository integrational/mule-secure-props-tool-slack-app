#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

./get-secure-props-tool.sh
./gradlew clean build -Dquarkus.package.type=uber-jar
