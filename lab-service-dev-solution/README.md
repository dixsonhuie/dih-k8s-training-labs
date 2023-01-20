# Smart-DIH-K8s-training - lab-solution

## 	Service Development – build new service from scratch

###### Lab Goals
1.  Understand how to build simple spring boot application that connects to space
2.  Understand how to deploy the new service on k8s environment
###### Lab Description
This lab includes 1 solution in which we will perform the tasks required to implement a new simple service. 
Use the slides from the lesson as a reference.
## 1 Lab setup
Make sure you EKS cluster created and Kubectl+helm are installed and configure to connect to Eks

1.1 start a demo in your local desktop - (gs-home/bin/gs.sh demo)
1.2 In IDE terminal run: mvn clean spring-boot:run 
1.3 Open another terminal run the following:
curl -X POST localhost:8080/newtable -H 'Content-type:application/txt' -d 'CREATE TABLE Persons (ID int NOT NULL,LastName varchar(255) NOT NULL,FirstName varchar(255),Age int,PRIMARY KEY (ID))'
1.4 Go to ops-manger (localhost:8090) see type Persons was created
1.5 Run in terminal
curl -X POST localhost:8080/insert?tableName=Persons -H  'Content-type:application/json' -d '{"ID": 1, "LastName": "Levin", "FirstName" :"Avi", "Age":34 }'
curl -X POST localhost:8080/insert?tableName=Persons -H  'Content-type:application/json' -d '{"ID": 2, "LastName": "Choen", "FirstName" :"Roni", "Age":34 }'
1.6 see data in space using ops-ui
1.7 Run in terminal
curl -v localhost:8080/queryrs?tableName=Persons

2. Change for k8s environment
2.1 Change application_k8s properties to reflect paramters in your k8s enviorments
2.2 Rename application.properties to application_sg.properties & application_k8s.properties to application.properties
2.3 Run in terminal : mvn clean install
2.3 Build image: (create user in docker hub if you don't have one)
   docker build -t atzd1/myspring1:1.0.1 .
2.4 Push docker to docker hub
   docker  login
2.5 docker push atzd1/myspring1:1.0.1

3 Run your docker in k8s enviorment
3.1 Simple run
kubectl run mytest --image=atzd1/myspring1:1.0.1

If needed
kubectl port-forward mytest 8080:8080

Alternatives:
kubectl create deployment demo1 --image=atzd1/myspring1:1.0.1   --dry-run=client -o=yaml > deployment.yaml

In this case deployment file is created and can be edited

kubectl create service clusterip demo1 --tcp=8080:8081 --dry-run=client -o=yaml >> deployment.yaml
Edit deployment change type for LoadBalancer or use ingress to expose the service

kubectl apply -f deployment.yaml

3.5 Repeat 1 in k8s environment replacing local host and port with relevant paramters in your enviorment



   

    
