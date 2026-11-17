# Etapa 1: compila o projeto com Maven e gera o .jar
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia so o necessario para baixar as dependencias (aproveita o cache do Docker)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copia o codigo e gera o jar (sem rodar os testes)
COPY src ./src
RUN ./mvnw -B -DskipTests clean package

# Etapa 2: imagem final, so com o Java (JRE) e o .jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/redeviva-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]