FROM java:8
VOLUME /java
ENV JAVA_CACERTS /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security
ADD c2s.cer /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security
RUN keytool -import -noprompt -alias c2s -keystore  ${JAVA_CACERTS}/cacerts -storepass changeit -file ${JAVA_CACERTS}/c2s.cer
ARG finalName
ADD $finalName /patient-registration.jar
ENTRYPOINT ["java","-jar","/patient-registration.jar","--spring.profiles.active=docker"]