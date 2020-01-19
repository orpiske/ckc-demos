FROM registry.access.redhat.com/ubi8/ubi-minimal AS buildimg
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ARG CKC_DEMOS_VERSION
ENV CKC_DEMOS_VERSION ${CKC_DEMOS_VERSION:-0.0.1-SNAPSHOT}
ARG CKC_DEMOS_BRANCH
ENV CKC_DEMOS_BRANCH ${CKC_DEMOS_BRANCH:-master}
LABEL CKC_DEMOS_VERSION=${CKC_DEMOS_VERSION}
RUN microdnf install -y java-1.8.0-openjdk-devel which unzip zip wget
ENV JAVA_HOME /etc/alternatives/jre
WORKDIR /root/build
RUN wget https://github.com/orpiske/ckc-demos/archive/${CKC_DEMOS_BRANCH}.zip -O ckc-demos.zip && unzip ckc-demos.zip && ln -s ckc-demos-${CKC_DEMOS_BRANCH} ckc-demos
RUN cd ckc-demos && ./mvnw -PPackage -DskipTests=true clean package


#FROM registry.access.redhat.com/ubi8/ubi-minimal AS ckc-demos
#ARG CAMEL_KAFKA_CONNECTOR_VERSION
#ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
#USER root:root
#
#USER 1001