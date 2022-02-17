#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

echo $'\nDeploying via plain Kubernetes resources to Kubernetes cluster\n'
# don't kubectl-apply the resources file directly, but instead
# first use sed to replace placeholder template vars {{var}} with actual secret values from env vars,
# only then kubectl-apply
cat k8s-resources.yaml                                              \
  | sed -e 's,{{APP}},'"$APP"',g'                                   \
        -e 's,{{IMG}},'"$IMG"',g'                                   \
        -e 's,{{SLACK_SIGNING_SECRET}},'"$SLACK_SIGNING_SECRET"',g' \
        -e 's,{{SLACK_APP_TOKEN}},'"$SLACK_APP_TOKEN"',g'           \
        -e 's,{{SLACK_BOT_TOKEN}},'"$SLACK_BOT_TOKEN"',g'           \
  | kubectl apply -f -
# trigger rollout (necessary because using image tag "latest" in Deployment's pod template, so the above apply only triggers a rollout upon creation but not upon update)
kubectl rollout restart deployment/$APP -n $APP
