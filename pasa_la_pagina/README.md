# Proyecto Intercambio de Materiales

Este es el backend en **Spring Boot** para la aplicación de intercambio de libros y apuntes.  

---

## 🔹 Requisitos
- Java 21 (o superior)
- Maven
- Docker y Docker Compose

---

## 🔹 Configuración de la base de datos (MySQL con Docker)

1. Crear un archivo `.env` en la raíz del proyecto (donde está `docker-compose.yml`) con el siguiente contenido:

```env
MYSQL_ROOT_PASSWORD={password_root}
MYSQL_DATABASE={name_db = pasa_la_pagina}
MYSQL_USER={name_user}
MYSQL_PASSWORD={password_user}