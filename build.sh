#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

# builds uber-jar as specified in application.properties
./gradlew clean build
