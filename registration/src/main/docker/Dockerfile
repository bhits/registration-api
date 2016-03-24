FROM java:8
VOLUME /java
ADD patient-registration-1.4.0-SNAPSHOT.jar /patient-registration.jar
ENTRYPOINT ["java","-Djavax.net.ssl.trustStore=/java/keystore/mhc.keystore","-Djavax.net.ssl.trustStorePassword=changeit","-jar","/patient-registration.jar"]