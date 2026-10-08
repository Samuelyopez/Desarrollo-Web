# dogtor-api

API REST de la veterinaria DogTor (Spring Boot 3.5 + JPA + H2). La consume `dogtor-angular`.

## Ejecutar

Requiere JDK 17 o superior.

```bash
./mvnw spring-boot:run
```

| Qué | URL |
|---|---|
| API | http://localhost:8090 |
| Swagger UI | http://localhost:8090/swagger-ui/index.html |
| Consola H2 | http://localhost:8090/h2 (JDBC URL `jdbc:h2:file:./data/dogtordb`, usuario `sa`, sin contraseña) |

La base de datos queda en `data/dogtordb.mv.db` (ignorada por git). Borra la carpeta `data/` para reiniciar los datos de prueba.

## Estructura

`controller` (REST, `@CrossOrigin` para `localhost:4200`) → `service` (interfaz + implementación) → `repository` (JPA) → `entities`. Los errores se traducen a códigos HTTP en `errors`.
