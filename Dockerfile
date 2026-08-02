FROM maven:3.9.5-eclipse-temurin-21 as builder
WORKDIR /app
COPY Backend/pom.xml .
COPY Backend/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/app.jar .
EXPOSE 8081
CMD ["java", "-Dserver.port=8081", "-jar", "app.jar"]
