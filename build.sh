#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

./gradlew build -Dquarkus.package.type=uber-jar
