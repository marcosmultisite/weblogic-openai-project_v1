FROM container-registry.oracle.com/middleware/weblogic:12.2.1.4

USER root
RUN mkdir -p /u01/apps
COPY target/weblogic-openai.war /u01/apps/

USER oracle
ENV DEBUG=true

EXPOSE 7001
