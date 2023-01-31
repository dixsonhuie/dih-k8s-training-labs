#!/bin/bash
SCRIPT=$(realpath "$0")
SCRIPTPATH=$(dirname "$SCRIPT")
cd $SCRIPTPATH

# Delete xap umbrella
echo "Deleting DIH umbrella ..."
helm uninstall central-wangw-sink central-space
helm uninstall xap
helm uninstall ingress-nginx

# Delete k8s-dashboard
echo "Deleting k8s-dashboard"
kubectl delete -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml

kubectl delete svc sink-service
