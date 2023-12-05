FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
#USER root
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]