# ====================
# ETAPA DE BUILD
# ====================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos solo la carpeta del proyecto
COPY pasa_la_pagina ./pasa_la_pagina

# Vamos al directorio del proyecto
WORKDIR /app/pasa_la_pagina

# Damos permisos al Maven Wrapper
RUN chmod +x mvnw

# Construimos el proyecto sin tests
RUN ./mvnw -DskipTests clean package

# ====================
# ETAPA DE RUNTIME
# ====================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado en la etapa de build
COPY --from=build /app/pasa_la_pagina/target/*.jar app.jar

# Puerto en el que escucha Spring Boot
EXPOSE 8080

# Comando para iniciar el backend
ENTRYPOINT ["java", "-jar", "app.jar"]