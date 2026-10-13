# DogTor 🐾 - Clínica veterinaria

Aplicativo web para una clínica veterinaria que hospitaliza perros y gatos. Tiene tres portales:

- **Cliente (dueño):** ve sus mascotas y los tratamientos que han recibido.
- **Veterinario:** gestiona dueños y mascotas, da tratamientos y consulta sus pacientes.
- **Administrador:** gestiona a los veterinarios y ve el dashboard del negocio.

| Proyecto | Tecnología | Puerto |
|---|---|---|
| [`dogtor-api/`](dogtor-api/README.md) | Spring Boot 3.5 (Java 17), JPA, H2 en archivo, Swagger, Apache POI | 8090 |
| [`dogtor-angular/`](dogtor-angular/README.md) | Angular 19, RxJS, Bootstrap 5 | 4200 |

Angular consume la API REST con `HttpClient`. **No hay sesión, token ni guards**: el login valida correo y
contraseña y te lleva a tu portal, y la cédula de quien lo usa va en la URL (`/vet/800000001/...`).
Cualquier pantalla se abre solo con su ruta, también al recargar (F5) o con un enlace directo.

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

## Portales y rutas

| Portal | URL de inicio | Ejemplo |
|---|---|---|
| Cliente | `/cliente/<cédula del dueño>` | http://localhost:4200/cliente/1000000001 |
| Veterinario | `/vet/<cédula del veterinario>` | http://localhost:4200/vet/800000001 |
| Administrador | `/admin` | http://localhost:4200/admin |

| Funcionalidad | Portal |
|---|---|
| Mis mascotas e historial de tratamientos | Cliente |
| CRUD de dueños (por cédula) | Veterinario |
| CRUD de mascotas y activar/desactivar | Veterinario |
| Dar tratamiento, Mis pacientes | Veterinario (activo) |
| CRUD de veterinarios y estado laboral | Administrador |
| Dashboard | Administrador |

Cómo funciona el acceso:

- **Login:** valida correo y contraseña en la API (401 si son incorrectos, 403 si el usuario está inactivo) y
  redirige al portal de su rol con su cédula en la URL. No se guarda nada en el navegador.
- **Sin guards (decisión del proyecto):** las pantallas no se bloquean; quien conozca la URL puede abrirla.
  La navbar muestra los enlaces del portal según la URL en la que se está.
- **API:** las reglas de negocio se validan en el servidor: un veterinario inactivo no puede dar tratamientos y solo
  las mascotas activas reciben tratamiento (409). En el portal cliente, la API responde 404 si la mascota no es del
  dueño de la URL.
- **Limitación conocida:** sin sesión ni token no hay control de acceso por rol real (AC34): la API no sabe quién la
  llama. La mejora natural es Spring Security con JWT y `@PreAuthorize` por rol.
