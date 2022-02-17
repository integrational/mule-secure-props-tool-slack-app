#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

docker run -it --rm --name $APP -p 8080:8080 -v $(pwd):/app -w /app        \
       -e SLACK_SIGNING_SECRET -e SLACK_APP_TOKEN -e SLACK_BOT_TOKEN       \
       registry.access.redhat.com/ubi8/ubi-minimal:latest ./build/*-runner
