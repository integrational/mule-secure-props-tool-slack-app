#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

#  --pull always
docker run -it --rm --name $APP -p 3000:3000 -e SLACK_SIGNING_SECRET -e SLACK_APP_TOKEN -e SLACK_BOT_TOKEN -e MONGODB_UNAME -e MONGODB_PASSW $IMG
