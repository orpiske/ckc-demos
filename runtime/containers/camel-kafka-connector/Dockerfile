FROM registry.access.redhat.com/ubi8/ubi-minimal AS buildimg
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ARG CAMEL_KAFKA_CONNECTOR_VERSION
ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
ARG CAMEL_KAFKA_CONNECTOR_BRANCH
ENV CAMEL_KAFKA_CONNECTOR_BRANCH ${CAMEL_KAFKA_CONNECTOR_BRANCH:-strimzi-s2i}
ARG CAMEL_KAFKA_CONNECTOR_FORK
ENV CAMEL_KAFKA_CONNECTOR_FORK ${CAMEL_KAFKA_CONNECTOR_FORK:-orpiske}
ARG KAFKA_VERSION
ENV KAFKA_VERSION ${KAFKA_VERSION:-2.4.0}

LABEL CAMEL_KAFKA_CONNECTOR_VERSION=${CAMEL_KAFKA_CONNECTOR_VERSION}
RUN microdnf install -y java-1.8.0-openjdk-devel which unzip zip wget tar gzip
ENV JAVA_HOME /etc/alternatives/jre
WORKDIR /root/build
RUN wget https://github.com/${CAMEL_KAFKA_CONNECTOR_FORK}/camel-kafka-connector/archive/${CAMEL_KAFKA_CONNECTOR_BRANCH}.zip -O camel-kafka-connector.zip && unzip camel-kafka-connector.zip && ln -s camel-kafka-connector-${CAMEL_KAFKA_CONNECTOR_BRANCH} camel-kafka-connector
RUN cd camel-kafka-connector && ./mvnw -Dscope=compile -DskipTests=true clean package
RUN wget -c http://apache.miloslavbrada.cz/kafka/${KAFKA_VERSION}/kafka_2.12-${KAFKA_VERSION}.tgz && \
    mkdir -p /root/build/kafka && \
    tar --strip-components=1 -xvf kafka_2.12-${KAFKA_VERSION}.tgz -C /root/build/kafka

# FROM strimzi/kafka:0.16.0-kafka-2.4.0 as camel-kafka-connector
# ARG CAMEL_KAFKA_CONNECTOR_VERSION
# ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
# VOLUME /opt/kafka/custom-config/
# USER root:root
# RUN mkdir -p /opt/kafka/custom-config/ && touch /opt/kafka/custom-config/log4j.properties
# COPY --from=buildimg /root/build/camel-kafka-connector/core/target/camel-kafka-connector-${CAMEL_KAFKA_CONNECTOR_VERSION}-package/share/java/camel-kafka-connector/ /opt/kafka/plugins/camel-kafka-connector/
# COPY --from=buildimg /root/build/camel-kafka-connector/examples/ /opt/kafka/custom-config
# USER 1001

FROM registry.access.redhat.com/ubi8/ubi-minimal AS ckc-demos-base
ARG KAFKA_VERSION
ENV KAFKA_VERSION ${KAFKA_VERSION:-2.4.0}
ARG CAMEL_KAFKA_CONNECTOR_VERSION
ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
ENV KAFKA_HOME /opt/kafka/
WORKDIR ${KAFKA_HOME}
RUN microdnf install -y java-1.8.0-openjdk-headless && microdnf clean all
COPY --from=buildimg /root/build/kafka /opt/kafka
COPY --from=buildimg /root/build/camel-kafka-connector/core/target/camel-kafka-connector-${CAMEL_KAFKA_CONNECTOR_VERSION}-package/share/java/camel-kafka-connector/ /opt/camel-kafka-connector/
RUN echo "for file in $(ls /opt/camel-kafka-connector/*) ; do echo $file | tr '\n' ':' ; done" >> /opt/run-connector.sh

RUN echo "$KAFKA_HOME/bin/connect-standalone.sh $KAFKA_HOME/config/connect-standalone.properties config/CamelSinkConnector.properties " >> /opt/run-connector.sh