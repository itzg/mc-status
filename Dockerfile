FROM openjdk:8u171-jre-alpine

LABEL MAINTAINER=itzg

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/mc-status.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/mc-status.jar