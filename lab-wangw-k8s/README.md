## Lab - WanGW in k8s

### Prerequisties
1. EKS for region A (west) is instaled and accessible from your. 
2. EKS for region B (central) is instaled and accessible.
3. kubectl and aws-cli are installed.

### Configure the lab files
1. Edit west-eks.sh and update the file with your EKS cluster details (name and region)
   ```
   vi west-eks.sh

   aws eks update-kubeconfig --name TF-CSM-Shmulik-Test --region eu-west-1
   ```
2. Edit central-eks.sh and update the file with your EKS cluster details (name and region)
   ```
   vi central-eks.sh

   aws eks update-kubeconfig --name TF-CSM-Shmulik-Test --region eu-central-1
   ```
3. Verify you are able to connect both west and central EKS clusters
   ```
   ~/west-eks.sh
   kubectl get nodes

   NAME                                       STATUS   ROLES    AGE   VERSION
   ip-10-0-0-94.eu-west-1.compute.internal    Ready    <none>   49m   v1.21.14-eks-fb459a0
   ip-10-0-1-41.eu-west-1.compute.internal    Ready    <none>   49m   v1.21.14-eks-fb459a0
   ip-10-0-2-229.eu-west-1.compute.internal   Ready    <none>   49m   v1.21.14-eks-fb459a0

   ~/central-eks.sh
   kubectl get nodes

   NAME                                           STATUS   ROLES    AGE   VERSION
   ip-10-11-0-86.eu-central-1.compute.internal    Ready    <none>   54m   v1.21.14-eks-fb459a0
   ip-10-11-1-223.eu-central-1.compute.internal   Ready    <none>   54m   v1.21.14-eks-fb459a0
   ip-10-11-2-226.eu-central-1.compute.internal   Ready    <none>   54m   v1.21.14-eks-fb459a0
   ```
4. Install dih umbrella on west EKS cluster
   ```
   ~/west-eks.sh
   ./west-install-dih-umbrella.sh
   ```
   Verify by:
   ```
   kubectl get svc,pods
   
   NAME                                         TYPE           CLUSTER-IP       EXTERNAL-IP                                                                  PORT(S)                                        AGE
   service/ingress-nginx-controller             LoadBalancer   172.20.115.176   ab47d66dcf51f42f788e423e939cbeee-1251519855.eu-central-1.elb.amazonaws.com   80:32086/TCP,443:31086/TCP                     15m
   service/ingress-nginx-controller-admission   ClusterIP      172.20.202.169   <none>                                                                       443/TCP                                        15m
   service/kubernetes                           ClusterIP      172.20.0.1       <none>                                                                       443/TCP                                        61m
   service/sink-service                         ClusterIP      172.20.250.107   <none>                                                                       8200/TCP                                       15m
   service/webhook-server                       ClusterIP      172.20.55.140    <none>                                                                       443/TCP                                        16m
   service/xap-grafana                          ClusterIP      172.20.1.238     <none>                                                                       3000/TCP                                       16m
   service/xap-influxdb                         ClusterIP      172.20.98.117    <none>                                                                       8086/TCP,8088/TCP                              16m
   service/xap-xap-manager-hs                   ClusterIP      None             <none>                                                                       2181/TCP,2888/TCP,3888/TCP,4174/TCP            16m
   service/xap-xap-manager-service              LoadBalancer   172.20.55.72     aa79a55dfb9cf4e348cf7593287acbda-89852023.eu-central-1.elb.amazonaws.com     8090:31535/TCP,4174:30117/TCP,8200:30740/TCP   16m

   NAME                                            READY   STATUS    RESTARTS   AGE
   pod/ingress-nginx-controller-666f45c794-8fxdp   1/1     Running   0          15m
   pod/xap-grafana-5574c8bcd5-qxqw6                1/1     Running   0          16m
   pod/xap-influxdb-0                              1/1     Running   0          16m
   pod/xap-operator-54c7d9785-bjwp7                1/1     Running   0          16m
   pod/xap-xap-manager-0                           1/1     Running   0          16m
   ```
5. Edit the ingress controller
   ```
   kubectl edit deployments ingress-nginx-controller
   ```
   Go to the 'containers:' section and under 'args:' add this line: (Don't use TAB for indentation but spaces only.)
   ``` 
   - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
   ```
   It should be like:
   ```
   spec:
   containers:
   - args:
     - /nginx-ingress-controller
     - --publish-service=$(POD_NAMESPACE)/ingress-nginx-controller
     - --election-id=ingress-nginx-leader
     - --controller-class=k8s.io/ingress-nginx
     - --ingress-class=nginx
     - --configmap=$(POD_NAMESPACE)/ingress-nginx-controller
     - --validating-webhook=:8443
     - --validating-webhook-certificate=/usr/local/certificates/cert
     - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
   ```
   Press Esc, and type:
   ```
   :wq
   ```
   Press Enter. Validate you got the message:
   ```
   deployment.apps/ingress-nginx-controller edited
   ```
6. Edit the ingress service
   ```
   kubectl edit svc ingress-nginx-controller
   ```
   Go to 'Ports:' section and ADD the following lines: (Don't use TAB for indentation but spaces only.)
   ```
   - name: lus
     nodePort: 30420
     port: 4174
   - name: comm
     nodePort: 30422
     port: 8200
   - name: manager
     nodePort: 30428
     port: 8090
   - name: grafana
     nodePort: 30432
     port: 3000
    ```   
    Press Esc, and type:
    ```
    :wq
    ```
    Press Enter. Validate you got the message:
    ```
    service/ingress-nginx-controller edited
    ```
 7. Locate the ingress public ip:
    ```
    kubectl describe svc ingress-nginx-controller |grep 'LoadBalancer Ingress:'
    ```
 8. Use a browser to access the ops-manager UI: http://\<LoadBalancer Ingress\>:8090



 9. Install dih umbrella on central EKS cluster
    ```
    ~/central-eks.sh
    ./central-install-dih-umbrella.sh
    ```
    Verify by:
    ```
    kubectl get svc,pods

    NAME                                         TYPE           CLUSTER-IP       EXTERNAL-IP                                                                  PORT(S)                                        AGE
    service/ingress-nginx-controller             LoadBalancer   172.20.115.176   ab47d66dcf51f42f788e423e939cbeee-1251519855.eu-central-1.elb.amazonaws.com   80:32086/TCP,443:31086/TCP                     20m
    service/ingress-nginx-controller-admission   ClusterIP      172.20.202.169   <none>                                                                       443/TCP                                        20m
    service/kubernetes                           ClusterIP      172.20.0.1       <none>                                                                       443/TCP                                        65m
    service/sink-service                         ClusterIP      172.20.250.107   <none>                                                                       8200/TCP                                       20m
    service/webhook-server                       ClusterIP      172.20.55.140    <none>                                                                       443/TCP                                        20m
    service/xap-grafana                          ClusterIP      172.20.1.238     <none>                                                                       3000/TCP                                       20m
    service/xap-influxdb                         ClusterIP      172.20.98.117    <none>                                                                       8086/TCP,8088/TCP                              20m
    service/xap-xap-manager-hs                   ClusterIP      None             <none>                                                                       2181/TCP,2888/TCP,3888/TCP,4174/TCP            20m
    service/xap-xap-manager-service              LoadBalancer   172.20.55.72     aa79a55dfb9cf4e348cf7593287acbda-89852023.eu-central-1.elb.amazonaws.com     8090:31535/TCP,4174:30117/TCP,8200:30740/TCP   20m

    NAME                                            READY   STATUS    RESTARTS   AGE
    pod/ingress-nginx-controller-666f45c794-8fxdp   1/1     Running   0          20m
    pod/xap-grafana-5574c8bcd5-qxqw6                1/1     Running   0          20m
    pod/xap-influxdb-0                              1/1     Running   0          20m
    pod/xap-operator-54c7d9785-bjwp7                1/1     Running   0          20m
    pod/xap-xap-manager-0                           1/1     Running   0          20m
    ```
 10. Repeat steps 5-8 for Central
 
 11. Review and understand the wangw configuration in the west-space code
     ```
     cd ~/deployments

     vim west_space/src/main/resources/META-INF/spring/pu.xml

     vim west_space/src/main/resources/pu.properties
     ```
     Review and understand the wangw configuration in the central-space code
     ```
     vim central_space/src/main/resources/META-INF/spring/pu.xml
    
     vim central_space/src/main/resources/pu.properties

 12. Prepare the west-wangw (Delegator)
     ```
     cd ~/deployments/west_delegator
     ```
     ```
     vim src/main/resources/pu.properties
     ```
     Edit:
     ``` 
     local-lookup-host=<insert here the west ingress ip>

     remote-lookup-host=<insert here the central ingress ip>
     ```
     Press Esc, type:
     ```
     :wq
     ```
     Press Enter.
     
     Build the Delegator:
     ```
     mv target target_old
     mvn package
     ```
     The new jar in located under the target folder.
     Upload the delegator jar to the s3:
     ```
     aws s3 cp --acl public-read target/west-delegator-1.0-SNAPSHOT.jar s3://csm-training/GSTM378-WANGW/
     ```
     Update the Mapper for west:
     ```
     cd ~/yaml

     vim west-wangw-delegator.yaml
     ```
     Update the arguments:
     ```
     -Dcom.gs.lus_lb=<insert here the west-ingress ip>
     -Dcom.gs.wangw_lb=<insert here the central-ingress ip>
     -Dcom.gs.remote_subnet_prefix=<insert here the prefix of central k8s cluster i.e: 10.11.>
     ```
     Save the file. (Esc, type :wq and Enter)




  13. Prepare the central-wangw (Sink)
      ```
      cd ~/deployments/central_sink
      ```
      ```
      vim src/main/resources/pu.properties
      ```
      Edit:
      ``` 
      local-lookup-host=<insert here the west ingress ip>

      remote-lookup-host=<insert here the central ingress ip>
      ```
      Press Esc, type:
      ```
      :wq
      ```
      Press Enter.
     
      Build the Delegator:
      ```
      mv target target_old
      mvn package
      ```
      The new jar in located under the target folder.
      Upload the delegator jar to the s3:
      ```    
      aws s3 cp --acl public-read target/central-sink-1.0-SNAPSHOT.jar s3://csm-training/GSTM378-WANGW/
      ```
      Update the Mapper for central:
      ```
      cd ~/yaml

      vim central-wangw-sink.yaml
      ```
      Update the arguments:
      ```
      -Dcom.gs.lus_lb=<insert here the central-ingress ip>
      -Dcom.gs.wangw_lb=<insert here the west-ingress ip>
      -Dcom.gs.remote_subnet_prefix=<insert here the prefix of west k8s cluster i.e: 10.0.>
      ```
      Save the file. (Esc, type :wq and Enter)
 14. Deploy west-space
     ```
     ~/west-eks.sh
     cd ~/yaml

     helm install west-space gigaspaces/xap-pu --version 16.2.1 -f west-space.yaml
     ```
     Deploy the west-wangw (Delegator)
     ```
     helm install west-wangw-delegator gigaspaces/xap-pu --version 16.2.1 -f west-wangw-delegator.yaml
     ```

 15. Deploy central-space
     ```
     ~/central-eks.sh
     cd ~/yaml

     helm install central-space gigaspaces/xap-pu --version=16.2.1 -f central-space.yaml
     ```
     Deploy the central-wangw (Sink)
     ```
     helm install central-wangw-sink gigaspaces/xap-pu --version 16.2.1 -f central-wangw-sink.yaml
     ```

 16. Verify that Delegator and Sink are CONNECTED
     By reviewing the logs of sink and delegator (use the UI or kubectl logs)
     Search for the message:
     In central (sink) 
     ```
     central-wangw-sink INFO [com.gigaspaces.replication.gateway.delegator.west-wangw] - Connection delegator: moved to state: CONNECTED
     ```
     In west (Delegator)
     ```
     central-wangw-sink INFO [com.gigaspaces.replication.gateway.delegator.west-wangw] - Connection delegator: moved to state: CONNECTED
     ```
 17. Deploy a feeder on west:
     ```
     ~/west-eks.sh
     cd ~/yaml

     helm install west-feeder gigaspaces/xap-pu --version 16.2.1 -f west-feeder.yaml
     ```
     Validate the west-space is being loaded with new entries.
     Validate the central-space is receiving data from west-space.

 18. The REDOLOG  - test it
     Explain and demonstrate how redlog works, for example: undeploy the Sink(central-wangw) and see how redlog in west-space growth up.

     Stop the WanGW replication by undeploying the Sink service:
     ```
     ~/central-eks.sh
     
     helm uninstall central-wangw-sink
     ```
     Go to the west-space service (ops-manager) and check: Is the RedoLog growing??
     Re-new the replication by deploying the sink service again:
     ```
     cd ~/yaml

     helm install central-wangw-sink gigaspaces/xap-pu --version 16.2.1 -f central-wangw-sink.yaml
     ```
     Go to the west-space service (ops-manager) and check: Is the RedoLog decreasing?

---------------------

### Uninstall all the components:

   1. Uninstall west:
      ```
      ~/west-eks.sh
      cd ~
      ./west-uninstall-dih-umbrella.sh
      ```
      Wait a few minutes.
      Validate by:
      ```
      helm list
      kubectl get svc -A
      ```
   2. Uninstall central
      ```
      ~/central-eks.sh
      cd ~
      ./central-uninstall-dih-umbrella.sh
      ```
      Wait a few minutes.
      Validate by:
      ```
      helm list
      kubectl get svc -A
      ```