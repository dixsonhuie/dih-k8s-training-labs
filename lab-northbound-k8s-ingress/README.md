

Run the ./install-dih-umbrella.sh only

cd dih-k8s-training-labs/lab-northbound-k8s-ingress/restexample

mvn package

# Need to host jars somewhere... how should this be done?

Install ingress controller
helm install
helm repo add nginx-stable https://helm.nginx.com/stable

helm repo update

helm install my-nginx nginx-stable/nginx-ingress --set rbac.create=true

Immediately after the install run:
kubectl patch svc my-nginx-nginx-ingress -p '{"metadata":{"annotations":{"service.beta.kubernetes.io/aws-load-balancer-additional-resource-tags":"Owner=owner,Project=gstm376,Name=ingress"}}}'

Install space

# remember to host jar
helm install demospace gigaspaces/xap-pu --version 16.2.1 --set manager.name=xap --set schema=partitioned,partitions=1,resourceUrl=https://github.com/dixsonhuie/mirrorexample2/blob/master/pu.basic.jar?raw=true,properties=space.name=demo

Install web PU
# remember to host jar
helm install rest gigaspaces/xap-pu --version 16.2.1 --set manager.name=xap --set instances=1,resourceUrl=https://github.com/dixsonhuie/mirrorexample2/blob/master/restserver.war?raw=true,properties=web.port=8091,metrics.enabled=false,livenessProbe.enabled=false,readinessProbe.enabled=false

# expose the service
kubectl apply -f pu-service.yaml

#
Modify the host value in ingress.yaml
check the external-ip given by load nginx service

```
[centos@ip-172-31-24-138 scratch]$ kubectl get service
NAME                      TYPE           CLUSTER-IP       EXTERNAL-IP                                                               PORT(S)
demospace-hs              ClusterIP      None             <none>                                                                    <none>
demospace-xap-pu-hs       ClusterIP      None             <none>                                                                    <none>
grafana-lb                LoadBalancer   172.20.198.233   a0f9fa770bb624545aac601897cae40a-920489994.eu-west-1.elb.amazonaws.com    3000:30974/TCP
kubernetes                ClusterIP      172.20.0.1       <none>                                                                    443/TCP
manager-loadbalancer      LoadBalancer   172.20.145.53    a3757173d66a949ba9cab9f1fde02e14-1745997678.eu-west-1.elb.amazonaws.com   8200:31232/TCP,4174:31448/TCP,8090:32388/TCP
my-nginx-nginx-ingress    LoadBalancer   172.20.234.34    a5e6192ef60594ebd86a0c9e395ad40a-1744496120.eu-west-1.elb.amazonaws.com   80:31332/TCP,443:31166/TCP
pu-service                ClusterIP      172.20.97.69     <none>                                                                    8091/TCP
rest-hs                   ClusterIP      None             <none>                                                                    <none>
rest-xap-pu-hs            ClusterIP      None             <none>                                                                    <none>
webhook-server            ClusterIP      172.20.80.47     <none>                                                                    443/TCP
xap-grafana               ClusterIP      172.20.76.186    <none>                                                                    3000/TCP
xap-influxdb              ClusterIP      172.20.221.35    <none>                                                                    8086/TCP,8088/TCP
xap-xap-manager-hs        ClusterIP      None             <none>                                                                    2181/TCP,2888/TCP,3888/TCP,4174/TCP
xap-xap-manager-service   LoadBalancer   172.20.63.145    adf2d83acb7bd439093da78d3650c903-434495312.eu-west-1.elb.amazonaws.com    8090:31309/TCP,4174:32628/TCP,8200:31208/TCP
```

kubectl apply -f ingress.yaml


http://<host name provisioned by eks>/rest <- Hello World!
http://<host name provisioned by eks>/rest/rest/restful-example <- Shows the count of objects in the demo space.




