## GSTM-378 WAN Gateway for k8s - Live DEMO

### Prerequisites
1. EKS cluster in region eu-west-1 is installed
2. EKS cluster in region eu-central-1 is installed
3. Docker images contain the Mapper.jar

* Note: It is mandatory to create each EKS cluster with a different subnet. For example: west: 10.0.x.x   central: 10.11.x.x 

* For EKS creation you can use this link: https://github.com/GigaSpaces-ProfessionalServices/OOTB-DIH-k8s-provisioning


### Demo steps:

1. connect to Jumper
   ```
   ssh -i OOTB-DIH-Provisioning.pem centos@3.248.214.83
   ```
2. Prepare the WEST side
   ```
   west.sh

   cd /home/centos/OOTB-DIH-k8s-provisioning-west/scripts

   ./install-dih-umbrella.sh
  
   ```
3. Edit the ingress controller
   ```
   kubectl edit deployments ingress-nginx-controller
   ```
   Go to the 'containers:' section and under 'args:' add this line: (Don't use TAB for indentation bu spaces only.)
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
    ```
    Press Esc, and type:
    ```
    :wq
    ```
    Press Enter.
    Validate you got the message:
    ```
    deployment.apps/ingress-nginx-controller edited
    ```
4. Edit the ingress service
   ```
   kubectl edit svc ingress-nginx-controller
   ```
   Go to 'Ports:' section and ADD the following lines: (Don't use TAB for indentation bu spaces only.)
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
    Press Enter.
    Validate you got the message:
    ```
    service/ingress-nginx-controller edited
    ``` 
5. Locate the ingress public ip:
   ```
   kubectl describe svc ingress-nginx-controller |grep 'LoadBalancer Ingress:'
   ```
6. From you browser, try to access the ops-manager by using: http://<ingress-public-ip>:8090

7. Prepare the Central side:
   ```
   central.sh

   cd /home/centos/OOTB-DIH-k8s-provisioning-central/scripts

   ./install-dih-umbrella.sh
  
   ```
   Repeat steps 3-6. 

8. Review and understand the the wangw configuration in the west-space code
   the space code
   ```
   cd ~/deployments
   ```
   ``` 
   vim west_space/src/main/resources/META-INF/spring/pu.xml
   ```
   ```
   vim west_space/src/main/resources/pu.properties
   ```

   Review and understand the the wangw configuration in the central-space code
   the space code
   ``` 
   vim central_space/src/main/resources/META-INF/spring/pu.xml
   ```
   ```
   vim central_space/src/main/resources/pu.properties
   ```

9. Prepare the west-wangw (Delegator)
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
   cd ~/OOTB-DIH-k8s-provisioning-west/yaml/

   vim west-wangw-delegator.yaml
   ```
   Update the arguments:
   ```
   -Dcom.gs.lus_lb=<insert here the west-ingress ip>
   -Dcom.gs.wangw_lb=<insert here the central-ingress ip>
   -Dcom.gs.remote_subnet_prefix=<insert here the prefix of central k8s cluster i.e: 10.11.>
   ```
   Save the file. (Esc, type :wq and Enter)




10. Prepare the central-wangw (Sink)
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
    cd ~/OOTB-DIH-k8s-provisioning-central/yaml/

    vim central-wangw-sink.yaml
    ```
    Update the arguments:
    ```
    -Dcom.gs.lus_lb=<insert here the central-ingress ip>
    -Dcom.gs.wangw_lb=<insert here the west-ingress ip>
    -Dcom.gs.remote_subnet_prefix=<insert here the prefix of west k8s cluster i.e: 10.0.>
    ```
    Save the file. (Esc, type :wq and Enter)
 11. Deploy west-space
     ```
     west.sh
     cd ~/OOTB-DIH-k8s-provisioning-west/yaml/

     helm install west-space gigaspaces/xap-pu --version 16.2.1 -f west-space.yaml
     ```
     Deploy the west-wangw (Delegator)
     ```
     helm install west-wangw-delegator gigaspaces/xap-pu --version 16.2.1 -f west-wangw-delegator.yaml
     ```

 12. Deploy central-space
     ```
     central.sh
     cd ~/OOTB-DIH-k8s-provisioning-central/yaml/

     helm install central-space gigaspaces/xap-pu --version=16.2.1 -f central-space.yaml
     ```
     Deploy the central-wangw (Sink)
     ```
     helm install central-wangw-sink gigaspaces/xap-pu --version 16.2.1 -f central-wangw-sink.yaml
     ```

 13. Verify that Delegator and Sink are CONNECTED
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
 14. Deploy a feeder on west:
     ```
     west.sh
     cd ~/OOTB-DIH-k8s-provisioning-west/yaml/

     helm install west-feeder gigaspaces/xap-pu --version 16.2.1 -f west-feeder.yaml
     ```
     Validate the west-space is being loaded with new entries.
     Validate the central-space is receiving data from west-space.

### Uninstall all the components:

   1. Uninstall west:
      ```
      west.sh
      cd ~/OOTB-DIH-k8s-provisioning-west/scripts
      ./uninstall-dih-umbrella.sh
      ```
      Wait a few minutes.
      Validate by:
      ```
      helm list
      kube  get svc -A
      ```
   2. Uninstall central
      ```
      central.sh
      cd ~/OOTB-DIH-k8s-provisioning-central/scripts
      ./uninstall-dih-umbrella.sh
      ```
      Wait a few minutes.
      Validate by:
      ```
      helm list
      kube  get svc -A
      ```
   



    
