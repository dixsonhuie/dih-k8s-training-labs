#!/bin/bash
SCRIPT=$(realpath "$0")
SCRIPTPATH=$(dirname "$SCRIPT")
cd $SCRIPTPATH
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl apply -f yaml/dashboard-adminuser.yaml
kubectl apply -f yaml/clusterRoleBinding.yaml
#kubectl apply -f ../yaml/k8s-dashboard-lb.yaml




