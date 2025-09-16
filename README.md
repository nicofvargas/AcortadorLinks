# Shorty: Acortador de URLs con Spring Boot

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![JPA / Hibernate](https://img.shields.io/badge/JPA%20%2F%20Hibernate-blueviolet)
![H2 Database](https://img.shields.io/badge/H2%20Database-orange)
![Maven](https://img.shields.io/badge/Maven-red)

Un servicio RESTful simple pero robusto para acortar URLs, construido con Spring Boot. Este proyecto sirve como una demostración práctica de los principios de diseño de software limpio, arquitectura por capas y mejores prácticas en el desarrollo de APIs.

## ✨ Características Principales

*   **Acortamiento de URLs:** Convierte URLs largas en códigos cortos únicos y fáciles de compartir.
*   **Redirección Rápida:** Redirige los códigos cortos a sus URLs originales de forma eficiente.
*   **Sin Colisiones:** Utiliza un algoritmo de conversión **Base62** basado en el ID autoincremental de la base de datos para garantizar que cada código corto sea 100% único.
*   **Prevención de Duplicados:** Si una URL larga ya ha sido acortada, el sistema devuelve el código existente en lugar de crear uno nuevo, ahorrando recursos.
*   **Seguimiento de Visitas:** Cuenta el número de veces que se utiliza cada link corto.
*   **Validación de Entrada:** Asegura que solo se procesen URLs válidas y bien formadas.
*   **API RESTful Profesional:** Sigue las mejores prácticas de diseño de API, incluyendo el uso de DTOs, códigos de estado HTTP correctos (`201 Created`, `302 Found`, `404 Not Found`) y manejo de errores.

## 🚀 Tecnologías Utilizadas

*   **Framework:** Spring Boot 3.x
*   **Lenguaje:** Java 21
*   **Persistencia:** Spring Data JPA / Hibernate
*   **Base de Datos de Desarrollo:** H2 Database (en memoria)
*   **Validación:** Jakarta Bean Validation (Hibernate Validator)
*   **Construcción:** Apache Maven
*   **Utilidades:** Lombok para reducir código repetitivo.

## 🛠️ Cómo Empezar

### Prerrequisitos

*   JDK 21 o superior.
*   Apache Maven 3.8 o superior.

### Ejecución Local

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/tu-repositorio.git
    cd tu-repositorio
    ```

2.  **Ejecuta la aplicación usando el Maven Wrapper:**
    *   En Windows:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   En macOS / Linux:
        ```bash
        ./mvnw spring-boot:run
        ```

3.  La aplicación estará disponible en `http://localhost:8080`.

4.  **(Opcional) Acceder a la Base de Datos H2:**
    *   Navega a `http://localhost:8080/h2-console` en tu navegador.
    *   Asegúrate de que el campo **JDBC URL** esté configurado como `jdbc:h2:mem:testdb`.
    *   Usuario: `sa`, Contraseña: (en blanco).

## 📖 API Endpoints

### Acortar una nueva URL

*   **Endpoint:** `POST /api/v1/shorten`
*   **Descripción:** Crea un nuevo link corto para la URL proporcionada.
*   **Request Body:**
    ```json
    {
      "longUrl": "https://www.ejemplo-largo.com/con/una/ruta/muy/especifica"
    }
    ```
*   **Respuesta Exitosa (201 Created):**
    ```json
    {
      "shortUrl": "http://localhost:8080/a",
      "shortCode": "a",
      "longUrl": "https://www.ejemplo-largo.com/con/una/ruta/muy/especifica",
      "creationDate": "2023-10-27T10:30:00",
      "expirationDate": null
    }
    ```

### Redirigir a la URL original

*   **Endpoint:** `GET /{shortCode}`
*   **Descripción:** Redirige al usuario a la URL larga original e incrementa el contador de visitas.
*   **Ejemplo de Petición:**
    ```
    GET http://localhost:8080/a
    ```
*   **Respuesta Exitosa:**
    *   **Código de Estado:** `302 Found`
    *   **Cabecera `Location`:** `https://www.ejemplo-largo.com/con/una/ruta/muy/especifica`
*   **Respuesta de Error:**
    *   **Código de Estado:** `404 Not Found` (si el `shortCode` no existe).

## 🏛️ Decisiones de Arquitectura

Este proyecto fue diseñado siguiendo principios de software limpio para maximizar la mantenibilidad y escalabilidad.

*   **Arquitectura por Capas:** Clara separación entre Controladores, Servicios y Repositorios.
*   **Principio de Responsabilidad Única (SRP):**
    *   Los **Servicios** están divididos por responsabilidad (`UrlShortenerService` para la creación, `UrlRedirectService` para la redirección).
    *   La lógica de conversión está aislada en un **Mapper** (`UrlMapper`).
    *   La generación de códigos está en una **Clase de Utilidad** (`Base62Converter`).
*   **Bajo Acoplamiento:** Se programa contra **interfaces** en la inyección de dependencias.
*   **DTO Pattern:** Se utilizan Data Transfer Objects para desacoplar la API de la capa de persistencia, mejorando la seguridad y la flexibilidad.

## 🔮 Futuras Mejoras

*   Implementar un `UrlStatsService` para exponer un endpoint de estadísticas de visitas.
*   Añadir la capacidad de crear links con fecha de expiración a través del DTO de petición.
*   Integrar Spring Security para permitir que los usuarios gestionen sus propios links.
*   Añadir perfiles de Spring para cambiar fácilmente de H2 a una base de datos persistente como PostgreSQL.

---
