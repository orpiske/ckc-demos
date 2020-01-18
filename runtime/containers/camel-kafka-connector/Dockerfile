FROM registry.access.redhat.com/ubi8/ubi-minimal AS buildimg
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ARG CAMEL_KAFKA_CONNECTOR_VERSION
ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
ARG CAMEL_KAFKA_CONNECTOR_BRANCH
ENV CAMEL_KAFKA_CONNECTOR_BRANCH ${CAMEL_KAFKA_CONNECTOR_BRANCH:-master}
LABEL CAMEL_KAFKA_CONNECTOR_VERSION=${CAMEL_KAFKA_CONNECTOR_VERSION}
RUN microdnf install -y java-1.8.0-openjdk-devel which unzip zip wget
ENV JAVA_HOME /etc/alternatives/jre
WORKDIR /root/build
RUN wget https://github.com/apache/camel-kafka-connector/archive/${CAMEL_KAFKA_CONNECTOR_BRANCH}.zip -O camel-kafka-connector.zip && unzip camel-kafka-connector.zip && ln -s camel-kafka-connector-${CAMEL_KAFKA_CONNECTOR_BRANCH} camel-kafka-connector
RUN cd camel-kafka-connector && ./mvnw -DskipTests=true clean package

FROM strimzi/kafka:0.16.0-kafka-2.4.0 as camel-kafka-connector
ARG CAMEL_KAFKA_CONNECTOR_VERSION
ENV CAMEL_KAFKA_CONNECTOR_VERSION ${CAMEL_KAFKA_CONNECTOR_VERSION:-0.0.1-SNAPSHOT}
USER root:root
RUN mkdir -p /opt/kafka/custom-config/ && touch /opt/kafka/custom-config/log4j.properties
COPY --from=buildimg /root/build/camel-kafka-connector/core/target/camel-kafka-connector-${CAMEL_KAFKA_CONNECTOR_VERSION}-package/share/java/camel-kafka-connector/ /opt/kafka/plugins/camel-kafka-connector/
COPY --from=buildimg /root/build/camel-kafka-connector/examples/ /opt/kafka/custom-config
USER 1001