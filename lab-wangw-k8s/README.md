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
   kubectl edit deployments  ingress-nginx-controller
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
