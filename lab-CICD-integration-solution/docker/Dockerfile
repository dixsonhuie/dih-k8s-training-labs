FROM openjdk:8-jdk-alpine

ARG VERSION
ENV KUBE_VERSION=1.20.2

RUN apk add --update --no-cache ca-certificates bash \
 && apk add --update --no-cache -t deps curl \
 && curl -L https://dl.k8s.io/release/v${KUBE_VERSION}/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl \
 && chmod +x /usr/local/bin/kubectl \
 && apk del --purge deps 

RUN wget https://get.helm.sh/helm-v3.11.0-linux-amd64.tar.gz
RUN mv helm-v3.11.0-linux-amd64.tar.gz /root/
#COPY helm-v3.5.1-linux-amd64.tar.gz /root/
RUN apk add --update \
    python3 \
    python3-dev \
    py-pip \
    build-base \
    && pip install awscli==1.19.112 --upgrade --user \
    && rm -rf /var/cache/apk/*
#RUN sudo apk add --no-cache \
#        python3 \
#        py3-pip \
#    && pip3 install --upgrade pip \
#    && pip3 install --no-cache-dir \
#        awscli \
#    && rm -rf /var/cache/apk/*
    
#RUN apk add --update \
#    curl \
#    && rm -rf /var/cache/apk/*

#RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 \
#    && chmod +x get_helm.sh && ./get_helm.sh

#RUN apk add --update ca-certificates && rm -rf /var/cache/apk/* && \
#  find /usr/share/ca-certificates/mozilla/ -name "*.crt" -exec keytool -import -trustcacerts \
#  -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -noprompt \
#  -file {} -alias {} \; && \
#  keytool -list -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts --storepass changeit

ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

WORKDIR /root
#ENTRYPOINT ["kubectl"]
#CMD ["help"]
