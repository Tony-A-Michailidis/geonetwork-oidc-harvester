FROM geonetwork:4.4.6

RUN apt-get update && apt-get install -y maven openjdk-11-jdk

COPY harvesters-oidc /usr/local/geonetwork/harvester/harvesters-oidc

RUN cd /usr/local/geonetwork && \
    mvn clean install -DskipTests && \
    cp web/target/geonetwork.war /usr/local/tomcat/webapps/

EXPOSE 8080
