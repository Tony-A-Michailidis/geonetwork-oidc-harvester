# --- Build stage ---
    FROM maven:3.9.9-eclipse-temurin-11 AS build
 
    # Copy the custom harvester into a build directory
    WORKDIR /build
    COPY harvesters-oidc /build/harvester/harvesters-oidc
    
    # Clone GeoNetwork 4.4.6 source code
    RUN git clone --branch 4.4.6 https://github.com/geonetwork/core-geonetwork.git geonetwork
    
    # Copy our harvester into the source
    RUN cp -r harvester/harvesters-oidc geonetwork/harvester/
    
    # Build GeoNetwork (skip tests)
    WORKDIR /build/geonetwork
    RUN mvn clean install -DskipTests
    
    # --- Final runtime stage ---
    FROM geonetwork:4.4.6
    
    # Copy the built WAR file into Tomcat
    COPY --from=build /build/geonetwork/web/target/geonetwork.war /usr/local/tomcat/webapps/geonetwork.war
    
    EXPOSE 8080
    