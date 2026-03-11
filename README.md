# 🐫 Camel Shop API - Backend

API RESTful robusta y escalable desarrollada con **Java 21** y **Spring Boot 3** para la gestión integral del E-commerce. El sistema centraliza la lógica de negocio, seguridad, manejo transaccional de pedidos y persistencia de datos en la nube.

![Java](https://img.shields.io/badge/Java-21-ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-336791)
![Docker](https://img.shields.io/badge/Docker-Railway-2496ED)

---

## 🚀 Funcionalidades Principales

- **Inventario Matricial:** Arquitectura de base de datos relacional capaz de manejar productos con múltiples variantes (talle y color) y descontar stock en tiempo real de forma transaccional.
- **Seguridad y Autorización:** Implementación de Spring Security 6 y JWT para proteger rutas y endopints del panel administrativo.
- **Gestión de Pedidos:** Creación de pedidos con lógica transaccional, evitando quiebres de stock por concurrencia.
- **Gestión Multimedia:** Integración directa con Cloudinary API para la carga, optimización y distribución de imágenes de productos y comprobantes.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3
- **ORM:** Spring Data JPA
- **Seguridad:** Spring Security 6 + JWT
- **Base de Datos:** PostgreSQL (alojada en Supabase)
- **Almacenamiento Cloud:** Cloudinary API
- **Validaciones:** Jakarta Bean Validation
- **Despliegue y Contenedores:** Docker + Railway

---

## ⚙️ Configuración del Entorno

Para ejecutar el proyecto de manera local, es necesario definir las siguientes variables de entorno:

### Base de Datos

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST>:<PORT>/postgres
SPRING_DATASOURCE_USERNAME=<USER>
SPRING_DATASOURCE_PASSWORD=<PASSWORD>
```

### Seguridad

```env
JWT_SECRET=<TU_CLAVE_BASE64_SEGURA>
JWT_EXPIRATION=86400000
```

### Cloudinary

```env
CLOUDINARY_CLOUD_NAME=<TU_CLOUD_NAME>
CLOUDINARY_API_KEY=<TU_API_KEY>
CLOUDINARY_API_SECRET=<TU_API_SECRET>
```

### CORS

```env
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://camelmodashop.vercel.app
```
---

## 🔌 Endpoints Principales

| Módulo    | Método | Endpoint                 | Descripción                     | Acceso  |
| --------- | ------ | ------------------------ | ------------------------------- | ------- |
| Auth      | POST   | `/api/auth/login`        | Autenticación de administrador  | Público |
| Productos | GET    | `/api/productos/publico` | Obtener catálogo activo         | Público |
| Productos | POST   | `/api/productos/admin`   | Crear producto con variantes    | Admin   |
| Pedidos   | POST   | `/api/pedidos/publico`   | Crear pedido (Checkout)         | Público |
| Uploads   | POST   | `/api/uploads`           | Subida de imágenes a Cloudinary | Admin   |

---

## 📦 Despliegue

La aplicación está preparada para despliegue continuo en Railway, utilizando contenedores Docker y variables de entorno para la configuración segura del entorno productivo.

---

## 👤 Autor
**Brian Battauz** - *Full Stack Developer*
[Portfolio](https://portfoliobrianbattauz.vercel.app/) | [LinkedIn](www.linkedin.com/in/brian-battauz-75691a217)