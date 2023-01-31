#!/bin/bash
SCRIPT=$(realpath "$0")
SCRIPTPATH=$(dirname "$SCRIPT")
cd $SCRIPTPATH

# Deploying xap umbrella
helm repo add gigaspaces https://resources.gigaspaces.com/helm-charts
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

# Install DIH Umbrella
helm install xap gigaspaces/xap --version 16.2.1 -f yaml/gigaspaces.yaml

#Install k8s dashboard
./install-k8s-dashboard.sh


# Install Ingress
helm install ingress-nginx ingress-nginx/ingress-nginx
kubectl apply -f yaml/central-ingress-cm.yaml
kubectl apply -f yaml/central-sink-svc.yaml

ingress_ip=$(kubectl describe svc ingress-nginx-controller |grep 'LoadBalancer Ingress:' |cut -d':' -f2  |tr -d "[:space:]")
sed -i "s/host:.*/host: ${ingress_ip}/g" yaml/ingress-rule-dashbord.yaml
sleep 10
kubectl apply -f yaml/ingress-rule-dashbord.yaml


# Annotate Load Balancers
cluster_name=$(kubectl config current-context |cut -d'/' -f2)
ops_manager_annotate="kubectl patch svc xap-xap-manager-service -p '{\"metadata\":{\"annotations\":{\"service.beta.kubernetes.io/aws-load-balancer-additional-resource-tags\":\"Owner=$cluster_name, Project=$cluster_name, Name=$cluster_name-opsManager-LB\"}}}'"
eval  $ops_manager_annotate
kubectl patch svc ingress-nginx-controller -p '{"metadata":{"annotations":{"service.beta.kubernetes.io/aws-load-balancer-additional-resource-tags":"Owner=CSM, Project=CSM, Name=wangw-ingress-LB"}}}'


echo "It will take a while for the load balancer to be available ..."
