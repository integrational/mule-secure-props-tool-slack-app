#!/bin/bash
set -Eeuo pipefail
cd $(cd "$(dirname "$0")" && pwd) # cd to where this script is located

docker run --pull always --rm -it                                     \
  -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e AWS_DEFAULT_REGION \
  -v /var/run/docker.sock:/var/run/docker.sock                        \
  -v $(pwd):/work -w /work                                            \
  integrational/eks-client /bin/bash -c "
    aws sts get-caller-identity
    aws eks update-kubeconfig --name $K8S_CLUSTER_NAME --region $K8S_CLUSTER_REGION > /dev/null
    kubectl cluster-info
    exec \$SHELL -l
  "
