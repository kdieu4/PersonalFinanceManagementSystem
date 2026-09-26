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

RUN groupadd --gid 10001 appgroup && \
    useradd --uid 10001 --gid appgroup --create-home appuser

WORKDIR /app
COPY --chown=appuser:appgroup --from=build /app/target/*.jar ./app.jar

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]