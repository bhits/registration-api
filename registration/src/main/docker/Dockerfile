FROM java:8
VOLUME /java
ARG finalName
ADD $finalName /patient-registration.jar
ENTRYPOINT ["java","-Djavax.net.ssl.trustStore=/java/keystore/mhc.keystore","-Djavax.net.ssl.trustStorePassword=changeit","-jar","/patient-registration.jar","--spring.profiles.active=docker"]