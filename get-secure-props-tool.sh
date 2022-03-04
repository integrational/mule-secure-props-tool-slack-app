#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

muleVersion=4.2
url=https://docs.mulesoft.com/downloads/mule-runtime/$muleVersion/secure-properties-tool.jar
outDir=src/main/resources
outFile=secure-properties-tool.jar

curl -L $url -o $outDir/$outFile
