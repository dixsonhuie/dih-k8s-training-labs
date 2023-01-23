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

### Test rest api application on local enviorment
1. start a demo in your local desktop - (gs-home/bin/gs.sh demo)
2. In IDE terminal run: mvn clean spring-boot:run
3. Open another terminal run the following:
curl -X POST localhost:8080/newtable -H 'Content-type:application/txt' -d 'CREATE TABLE Persons (ID int NOT NULL,LastName varchar(255) NOT NULL,FirstName varchar(255),Age int,PRIMARY KEY (ID))'
4. Go to ops-manger (localhost:8090) see type Persons was created
5. Run in terminal
curl -X POST localhost:8080/insert?tableName=Persons -H  'Content-type:application/json' -d '{"ID": 1, "LastName": "Levin", "FirstName" :"Avi", "Age":34 }'
curl -X POST localhost:8080/insert?tableName=Persons -H  'Content-type:application/json' -d '{"ID": 2, "LastName": "Choen", "FirstName" :"Roni", "Age":34 }'
6. see data in space using ops-ui
7. Run in terminal
curl -v localhost:8080/queryrs?tableName=Persons

### Change for k8s environment
1. connect to jumpper
2. run: ./install-dih-umbrella.sh
3. apply annotations changes so loadbulancers are not stopped
3. open ops-ui -> deploy space with name demo
4. Change application_k8s properties to reflect parameters in your k8s enviorments 
5. Rename application.properties to application_sg.properties & application_k8s.properties to application.properties
6. Run in terminal : mvn clean install

###  Build & push image
see: https://docs.docker.com/docker-hub/repos/#:~:text=To%20push%20an%20image%20to,docs%2Fbase%3Atesting%20
1. create a user in dockerhub
2. see DockerFile in the project and press open in terminal
3. docker build -t <your-hub-user>/<repo-name>[:<tag>] .
   (e.g : docker build -t atzd1/myrest:1.0.1 .)
4. docker  login
5. docker push <your-hub-user>/<repo-name>[:<tag>]
   (e.g : docker push myrest:1.0.1)

### Prepare and deploy service & deployment yamls 
1. edit mydeployment.yaml change image to the image you built
2. copy yaml files to your jumpper
3. in jumpper : kubectl apply -f mydeployment.yaml
4. validate a new pod was created
5. in jumpper : kubectl apply -f myservice.yaml

### Run curl against the k8s service
1. in jumpper run: kubectl get services 
2. see external ip for your service (my-rest-api service)
3. repeat first exercise replacing local host with the service host

note: Instead of using loadbalncer as type of service you can change to cluserIP and use ingress as explained in northbound lab]


   

    
