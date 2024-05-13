FROM openjdk:17-oracle
VOLUME /tmp
EXPOSE 8081
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} invoice-manager.jar
ENTRYPOINT ["java", "-jar", "invoice-manager.jar"]