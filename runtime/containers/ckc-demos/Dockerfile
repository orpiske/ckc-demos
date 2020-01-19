FROM registry.access.redhat.com/ubi8/ubi-minimal AS buildimg
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ARG CKC_DEMOS_VERSION
ENV CKC_DEMOS_VERSION ${CKC_DEMOS_VERSION:-0.0.1-SNAPSHOT}
ARG CKC_DEMOS_BRANCH
ENV CKC_DEMOS_BRANCH ${CKC_DEMOS_BRANCH:-master}
LABEL CKC_DEMOS_VERSION=${CKC_DEMOS_VERSION}
RUN microdnf install -y java-1.8.0-openjdk-devel which unzip zip tar wget gzip
ENV JAVA_HOME /etc/alternatives/jre
WORKDIR /root/build
RUN wget https://github.com/orpiske/ckc-demos/archive/${CKC_DEMOS_BRANCH}.zip -O ckc-demos.zip && unzip ckc-demos.zip && ln -s ckc-demos-${CKC_DEMOS_BRANCH} ckc-demos
RUN cd ckc-demos && ./mvnw -PPackage -DskipTests=true clean package

ENV CKC_DEMOS_APP_ROOT /opt/ckc-demos/
RUN mkdir -p ${CKC_DEMOS_APP_ROOT}
WORKDIR ${CKC_DEMOS_APP_ROOT}
ENV CKC_DEMO_TYPE kafka
RUN mkdir -p ${CKC_DEMO_TYPE}-client && tar --strip-components=1 -xvf /root/build/ckc-demos/clients/${CKC_DEMO_TYPE}-client/target/${CKC_DEMO_TYPE}-client-${CKC_DEMOS_VERSION}-bin.tar.gz -C ${CKC_DEMO_TYPE}-client
ENV CKC_DEMO_TYPE jms
RUN mkdir -p ${CKC_DEMO_TYPE}-client && tar --strip-components=1 -xvf /root/build/ckc-demos/clients/${CKC_DEMO_TYPE}-client/target/${CKC_DEMO_TYPE}-client-${CKC_DEMOS_VERSION}-bin.tar.gz -C ${CKC_DEMO_TYPE}-client


FROM registry.access.redhat.com/ubi8/ubi-minimal AS ckc-demos-base
ARG CKC_DEMOS_VERSION
ENV CKC_DEMOS_VERSION ${CKC_DEMOS_VERSION:-0.0.1-SNAPSHOT}
ENV CKC_DEMOS_APP_ROOT /opt/ckc-demos/
WORKDIR ${CKC_DEMOS_APP_ROOT}
RUN microdnf install -y java-1.8.0-openjdk-headless && microdnf clean all

FROM ckc-demos-base AS ckc-demos-client-kafka
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ENV CKC_DEMO_TYPE kafka
COPY --from=buildimg ${CKC_DEMOS_APP_ROOT}/ ${CKC_DEMOS_APP_ROOT}/${CKC_DEMO_TYPE}-client


FROM ckc-demos-base AS ckc-demos-client-jms
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ENV CKC_DEMO_TYPE jms
COPY --from=buildimg ${CKC_DEMOS_APP_ROOT}/ ${CKC_DEMOS_APP_ROOT}/${CKC_DEMO_TYPE}-client