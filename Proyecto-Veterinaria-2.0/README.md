# DogTor 🐾 - Clínica Veterinaria

**La solución definitiva, elegante y responsiva para la gestión integral de una clínica veterinaria.**

DogTor es una plataforma web completa que permite gestionar pacientes, agendar citas, y realizar compras en línea con un carrito interactivo, todo construido sobre una arquitectura MVC sólida y eficiente.

---

## ⚡ Quick Start

Clona el repositorio y levanta el proyecto en segundos usando Spring Boot.

```bash
git clone https://github.com/Samuelyopez/Desarrollo-Web.git
cd Desarrollo-Web/Sprint3
mvnw spring-boot:run
```

Una vez que el servidor inicie, visita `http://localhost:8080` en tu navegador.

---

## 📖 Introduction

Este proyecto representa el *Sprint 3* del desarrollo de la web para la clínica veterinaria DogTor. 
Se enfoca en integrar un diseño atractivo (UI/UX) utilizando Tailwind CSS y Bootstrap, junto con un robusto backend en Java (Spring Boot) para persistencia de datos (H2) y plantillas dinámicas (Thymeleaf). 

## 🛠 Prerequisites

Para ejecutar y modificar este proyecto necesitas:

- **Java 17** o superior
- **Maven** (o usar el wrapper `mvnw` incluido)
- Conexión a internet (para descargar dependencias como Tailwind y Alpine.js desde CDN)

## 🏗 Architecture Details

DogTor sigue un patrón de diseño **MVC (Model-View-Controller)**:

- **Frontend (View)**: 
  - Renderizado del lado del servidor con **Thymeleaf**.
  - Estilizado utilizando **Tailwind CSS** (vía CDN) y un módulo específico en **Bootstrap 5** (página Nosotros).
  - Interactividad del lado del cliente (ej. panel de carrito de compras deslizable) manejada con **Alpine.js**.
- **Backend (Controller & Service)**:
  - Spring Boot 3 gestionando las peticiones HTTP (`WebController.java`).
  - Servicios de negocio separados (`ProductoService`, `MascotaService`, etc.).
- **Database (Model)**:
  - Base de datos relacional **H2** en modo archivo persistente (`veterinariadb.mv.db`).
  - ORM manejado a través de **Spring Data JPA / Hibernate**.
  - Inicialización automática de datos dummy mediante `DataInitializer.java` (si la BD está vacía).

### Estructura de Páginas

| Ruta | Descripción |
|---|---|
| `/` | Landing page principal con carrusel de navegación |
| `/nosotros` | Historia de la clínica (Bootstrap 5) |
| `/equipo` | Perfil y especialidades de nuestros doctores |
| `/farmacia` | Tienda de productos medicinales con carrito de compras |
| `/accesorios` | Tienda de juguetes y accesorios con carrito de compras |
| `/adopciones` | Lista dinámica de mascotas buscando hogar |
| `/pacientes` | Panel administrativo de mascotas registradas (CRUD list) |
| `/citas` | Formulario de reserva de especialistas |
| `/login` | Acceso seguro para clientes |

## ✅ Expected Results

Al ejecutar la aplicación por primera vez:
1. La base de datos `veterinariadb.mv.db` se generará localmente.
2. Spring Boot inyectará automáticamente 5 dueños, 10 mascotas, 6 mascotas en adopción, y 12 productos (medicinales y recreacionales).
3. Podrás navegar fluidamente por todas las pestañas y el panel lateral del carrito de compras emergerá sin recargar la página.

## ⚠️ Troubleshooting

- **Problema**: Los productos o los doctores no aparecen actualizados.
  - **Solución**: Detén el servidor, elimina el archivo `veterinariadb.mv.db` en la carpeta raíz y reinicia. Esto forzará al `DataInitializer` a repoblar la base de datos con la última estructura.
- **Problema**: `java.net.BindException: Address already in use`.
  - **Solución**: El puerto 8080 está ocupado. Cierra cualquier otra aplicación corriendo en ese puerto o cambia `server.port=8081` en `application.properties`.

## 📚 Additional Resources

- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [Alpine.js Documentation](https://alpinejs.dev/)
