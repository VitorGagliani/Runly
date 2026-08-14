# Etapa 1: Build da aplicação Spring Boot
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B || true

COPY src src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagem final de execução leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
