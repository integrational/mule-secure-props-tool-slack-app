#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

# no longer works with Slack SDK in play!
./gradlew clean build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
