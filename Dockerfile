FROM eclipse-temurin:21-jdk AS build
LABEL authors="dieuhoang"

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve

COPY ./src ./src
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre AS run
LABEL authors="dieuhoang"
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]