# Estágio 1: Compilação rápida
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Execução leve e isolada
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Força o container a executar estritamente o Java, ignorando scripts travados
CMD ["java", "-jar", "app.jar"]