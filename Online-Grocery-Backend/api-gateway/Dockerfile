#FROM eclipse-temurin:17-jdk
#
#WORKDIR /app
#
#COPY target/*.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java",
#"-Dspring.config.import=optional:file:/config/application-common.yml",
#"-jar",
#"app.jar"]

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]