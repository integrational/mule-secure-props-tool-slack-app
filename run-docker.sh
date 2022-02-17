#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

# TODO: add --pull always
docker run -it --rm --name $APP -p 8080:8080 -e SLACK_SIGNING_SECRET -e SLACK_APP_TOKEN -e SLACK_BOT_TOKEN $IMG
