# ====================
# ETAPA DE BUILD
# ====================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos la carpeta del proyecto
COPY pasa_la_pagina ./pasa_la_pagina

# Entramos al directorio del proyecto
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

# Copiamos el jar generado
COPY --from=build /app/pasa_la_pagina/target/*.jar app.jar

# Exponemos el puerto (Railway asigna la variable PORT)
EXPOSE 8080

# Ejecutamos Spring Boot usando el PORT de Railway
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]