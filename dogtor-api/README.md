# dogtor-api

API REST de la veterinaria DogTor (Spring Boot 3.5 + JPA + H2). La consume `dogtor-angular`.

## Ejecutar

Requiere JDK 17 o superior.

```bash
./mvnw spring-boot:run   # API
./mvnw test              # pruebas (contexto y KPIs del dashboard)
```

| Qué | URL |
|---|---|
| API | http://localhost:8090 |
| Swagger UI | http://localhost:8090/swagger-ui/index.html |
| Consola H2 | http://localhost:8090/h2 (JDBC URL `jdbc:h2:file:./data/dogtordb`, usuario `sa`, sin contraseña) |

La base de datos queda en `data/dogtordb.mv.db` (ignorada por git) y sobrevive a los reinicios.
Borra la carpeta `data/` (con la API detenida) para volver a los datos de prueba.

## Estructura

`controller` (REST, `@CrossOrigin` para `localhost:4200`) → `service` (interfaz + implementación) → `repository` (JPA) → `entities`.
Los cuerpos de las peticiones son records en `dto` validados con `@Valid`; los errores se traducen a códigos HTTP en `errors`
(400 datos inválidos, 401 credenciales, 403 usuario inactivo, 404 no existe, 405 operación no permitida, 409 conflicto).

Las relaciones de las entidades llevan `@JsonIgnore`: el front pide los datos relacionados en peticiones aparte
(o usa campos derivados de solo lectura, como `duenoNombre` en la mascota).

## Endpoints

| Recurso | Endpoints |
|---|---|
| Login | `POST /api/auth/login` |
| Dueños (por cédula) | `GET /api/duenos?buscar=` · `GET /{cedula}` · `GET /{cedula}/mascotas` · `GET /{cedula}/mascotas/{id}` · `POST` · `PUT /{cedula}` · `DELETE /{cedula}` (borra sus mascotas, conserva los tratamientos) |
| Mascotas | `GET /api/mascotas?buscar=` · `GET /{id}` · `GET /{id}/tratamientos` · `POST` · `PUT /{id}` · `PUT /{id}/estado` (no se eliminan) |
| Medicamentos | `GET /api/medicamentos?disponibles=` |
| Tratamientos | `POST /api/tratamientos` (mascota activa, veterinario activo, descuenta unidades) |
| Veterinarios (por cédula) | `GET /api/veterinarios?buscar=&activo=` · `GET /{cedula}` · `POST` · `PUT /{cedula}` · `PUT /{cedula}/estado` · `GET /{id}/pacientes` |
| Dashboard | `GET /api/dashboard` |

El detalle de cada endpoint (cuerpos y respuestas) está en Swagger.

## Excel de medicamentos

`src/main/resources/excel/medicamentos.xlsx` se carga al iniciar (`MedicamentoExcelLoader`). Se puede apuntar a otro
archivo con la propiedad `dogtor.medicamentos.excel` (`classpath:` o `file:`).

- Hoja `Medicamentos` (o la primera) con la cabecera en la fila 1: **Nombre, Precio compra, Precio venta,
  Unidades disponibles, Unidades vendidas** (se reconocen sin importar tildes ni mayúsculas).
- Precios mayores a 0; unidades enteras mayores o iguales a 0. Las filas inválidas se registran en el log y se saltan.
- **Solo inserta los medicamentos que no existen** (por nombre). Así un reinicio no borra las unidades vendidas con la app.

## Indicadores del dashboard (AC29)

| KPI | Definición |
|---|---|
| Tratamientos del último mes | Tratamientos con fecha entre hoy − 30 días y hoy (y sus unidades) |
| Tratamientos por medicamento | Mismo periodo, agrupado por medicamento: nº de tratamientos y unidades |
| Veterinarios activos / inactivos | Según su estado laboral (`Usuario.activo`) |
| Mascotas totales / activas | Registradas hoy / las que están en la clínica (`activa = true`) |
| Ventas totales | Σ precio de venta × cantidad de todos los tratamientos |
| Ganancias totales | Σ (precio de venta − precio de compra) × cantidad |
| Top 3 | Medicamentos con más unidades en tratamientos (desempate: más ventas) |

Ventas, ganancias y unidades usan los precios copiados en cada tratamiento e incluyen los de mascotas eliminadas.
No incluyen las unidades vendidas históricas que trae el Excel, porque no tienen fecha ni precio del momento.
