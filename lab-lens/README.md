
# Lens for K8s

## Description
Setup Lens on a Windows machine to connect to DIH EKS cluster

<br>

## Prepare Windows Machine

1. Launch Windows EC2 instance (use Windows Server 2019 Base Image - Instance type: t3a.large)
2. Add tags:
> * Project=project name,
> * Owner=owner,
> * Name=name
> * profile=prod
> * or any policy other policy
3. Get the password for the Administrator user.
4. Connect to the Windows machine using RDP

<br>

## Download Components
| Component   | URL |
| :---        |    :---   |
| AWS CLI      | https://awscli.amazonaws.com/AWSCLIV2.msi |
| kubectl.exe    | https://dl.k8s.io/release/v1.26.0/bin/windows/amd64/kubectl.exe |
| Lens   | https://k8slens.dev/desktop.html |
| Google Chrome    | https://www.google.com/chrome/ |

<br>

## Installation
#### AWS CLI
> Run the installer (next > next ... ). Test it by running from the cmd:
>> *`aws --version`*

#### kubectl.exe
> Copy this file to some folder which is included in the PATH variable (like `c:\windows\system32`) or set another folder manually via "System Environment Variables". Test it by running from the cmd:
>> *`kubectl version`*

#### Lens
> run the exe file (next > next ...). You will have to provide an activation code, provided by a registration in Lens website

#### Google Chrome
> Install and run

<br>

## Configuration
#### AWS
Provide the AWS credentials for CSM-LAB user by running:
> *`aws configure`*

Validate the credentials by running:
> *`aws sts get-caller-identity`*

Get the EKS clusters and identify your CLUSTER_NAME in the list by running:
> *`aws eks list-clusters`*

Update your local kubeconfig file by running:
> *`aws eks update-kubeconfig --name <CLUSTER_NAME>`*

Test your kubectl and EKS cluster connectivity by running:
> *`kubectl get svc,nodes,pods`*

<br>

Launch Lens and your clusters (which are included in the kubeconfig) will be accessible.

<br>
