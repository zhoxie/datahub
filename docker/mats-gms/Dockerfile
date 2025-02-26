# Defining environment
ARG APP_ENV=prod

FROM adoptopenjdk/openjdk8:alpine-jre as base
ENV DOCKERIZE_VERSION v0.6.1
RUN apk --no-cache add curl tar wget \
    && curl https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/9.4.20.v20190813/jetty-runner-9.4.20.v20190813.jar --output jetty-runner.jar \
    && curl https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-jmx/9.4.20.v20190813/jetty-jmx-9.4.20.v20190813.jar --output jetty-jmx.jar \
    && curl https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-util/9.4.20.v20190813/jetty-util-9.4.20.v20190813.jar --output jetty-util.jar \
    && wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.4.1/opentelemetry-javaagent-all.jar \
    && wget https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.16.1/jmx_prometheus_javaagent-0.16.1.jar -O jmx_prometheus_javaagent.jar \
    && curl -L https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz | tar -C /usr/local/bin -xzv

FROM adoptopenjdk/openjdk8:alpine-slim as prod-build
RUN apk --no-cache add perl
COPY . .

FROM base as prod-install
COPY --from=prod-build /war.war /datahub/datahub-gms/bin/war.war
COPY --from=prod-build /entity-registry.yml /datahub/datahub-gms/resources/entity-registry.yml
COPY --from=prod-build /start.sh /datahub/datahub-gms/scripts/start.sh
COPY --from=prod-build /client-prometheus-config.yaml /datahub/datahub-gms/scripts/prometheus-config.yaml
RUN chmod 777 /datahub/datahub-gms/scripts/start.sh
RUN chmod 777 /datahub/datahub-gms/bin/war.war
RUN chmod 777 /datahub/datahub-gms/resources/entity-registry.yml
RUN chmod 777 /datahub/datahub-gms/scripts/prometheus-config.yaml

FROM base as dev-install
# Dummy stage for development. Assumes code is built on your machine and mounted to this image.
# See this excellent thread https://github.com/docker/cli/issues/1134

FROM ${APP_ENV}-install as final

RUN addgroup -S datahub && adduser -S datahub -G datahub
USER datahub

ENV JMX_OPTS=""
ENV JAVA_OPTS=""

EXPOSE 8080

HEALTHCHECK --start-period=2m --retries=4 CMD curl --fail http://localhost:8080/health || exit 1

CMD /datahub/datahub-gms/scripts/start.sh
