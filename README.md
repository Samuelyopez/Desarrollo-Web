# DogTor 🐾 - Clínica veterinaria

Aplicativo web para una clínica veterinaria que hospitaliza perros y gatos. Tiene tres portales:

- **Cliente (dueño):** ve sus mascotas y los tratamientos que han recibido.
- **Veterinario:** gestiona dueños y mascotas, da tratamientos y consulta sus pacientes.
- **Administrador:** gestiona a los veterinarios y ve el dashboard del negocio.

| Proyecto | Tecnología | Puerto |
|---|---|---|
| [`dogtor-api/`](dogtor-api/README.md) | Spring Boot 3.5 (Java 17), JPA, H2 en archivo, Swagger, Apache POI | 8090 |
| [`dogtor-angular/`](dogtor-angular/README.md) | Angular 19, RxJS, Bootstrap 5 | 4200 |

Angular consume la API REST con `HttpClient`. No hay sesión ni token: el login valida correo y contraseña
y el usuario queda en memoria en Angular (al recargar la página hay que volver a ingresar).

## Ejecutar

1. API (requiere JDK 17 o superior):

   ```bash
   cd dogtor-api
   ./mvnw spring-boot:run
   ```

   Al arrancar carga los medicamentos desde `src/main/resources/excel/medicamentos.xlsx` y, si la base está vacía,
   los datos de prueba. Para volver a los datos iniciales: detener la API y borrar la carpeta `dogtor-api/data/`.

2. Frontend:

   ```bash
   cd dogtor-angular
   npm install
   npm start
   ```

   Abrir http://localhost:4200.

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@dogtor.com`, `gerente@dogtor.com` | `admin123` |
| Veterinario | `veterinario@dogtor.com`, `laura.vet@dogtor.com`, `camilo.vet@dogtor.com`, `paula.vet@dogtor.com` | `vet123` |
| Veterinario inactivo | `pedro.vet@dogtor.com` (no puede ingresar) | `vet123` |
| Cliente | `juan.perez@correo.com`, `maria.lopez@correo.com`, … (los 10 dueños) | `cliente123` |

## Control de acceso por rol (AC34)

| Funcionalidad | Invitado | Cliente | Veterinario | Administrador |
|---|:-:|:-:|:-:|:-:|
| Landing | ✔ | ✔ | ✔ | ✔ |
| Login | ✔ | → su portal | → su portal | → su portal |
| Mis mascotas e historial de tratamientos | | ✔ | | |
| CRUD de dueños (por cédula) | | | ✔ | |
| CRUD de mascotas y activar/desactivar | | | ✔ | |
| Dar tratamiento, Mis pacientes | | | ✔ (solo activo) | |
| CRUD de veterinarios y estado laboral | | | | ✔ |
| Dashboard | | | | ✔ |

Cómo se aplica:

- **Angular:** cada portal (`/cliente`, `/vet`, `/admin`) tiene `canActivate` y `canActivateChild` con
  `authGuard` (sin sesión → `/login`) y `rolGuard` (rol ajeno → `/acceso-denegado`). La navbar muestra solo
  los enlaces del rol y el login redirige al portal de cada rol.
- **API:** las reglas de negocio se validan en el servidor: un usuario inactivo no puede iniciar sesión (403),
  un veterinario inactivo no puede dar tratamientos y solo las mascotas activas reciben tratamiento (409).
- **Limitación conocida:** como no hay token ni sesión (decisión del proyecto), la API no sabe quién la llama:
  cualquiera que la use directamente (por ejemplo desde Swagger) puede consultar o modificar datos.
  La mejora natural es Spring Security con JWT y `@PreAuthorize` por rol.
