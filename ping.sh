#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

host=localhost
port=3000
base="http://$host:$port"

# according to MicroProfile Health
curl -i $base/q/health/started
curl -i $base/q/health/live
curl -i $base/q/health/ready
curl -i $base/q/health

# about
curl -i $base/about
