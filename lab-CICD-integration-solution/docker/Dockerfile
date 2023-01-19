FROM alpine:3.14

ARG VERSION
ENV KUBE_VERSION=1.20.2

## Install JAVA ##
RUN apk add openjdk11

## Install Kubectl ##
RUN apk add --update --no-cache ca-certificates bash \
 && apk add --update --no-cache -t deps curl \
 && curl -L https://dl.k8s.io/release/v${KUBE_VERSION}/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl \
 && chmod +x /usr/local/bin/kubectl \
 && apk del --purge deps 

## Install HELM ##
RUN wget https://get.helm.sh/helm-v3.5.1-linux-amd64.tar.gz
RUN tar -zxvf helm-v3.5.1-linux-amd64.tar.gz
RUN mv linux-amd64/helm /usr/local/bin/helm

## Install AWS-CLI ##
RUN apk add --no-cache aws-cli
RUN aws --version   # Just to make sure its installed alright

## Install MAVEN ##
ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH
RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

WORKDIR /root