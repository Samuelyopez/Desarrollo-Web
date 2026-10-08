# DogTor 🐾 - Frontend en Angular

Frontend de DogTor en **Angular 19**: componentes standalone, servicios con `inject()`, entradas/salidas
(`input()` / `output()`), control de flujo (`@if`, `@for`), formularios reactivos y **RxJS**.
Los estilos usan **Bootstrap 5.3** + **Bootstrap Icons** y SCSS. Los precios y fechas se muestran en formato
colombiano (`es-CO`).

Los datos vienen de la API REST [`dogtor-api`](../dogtor-api/README.md) (`http://localhost:8090/api`,
configurada en `src/app/api.config.ts`): hay que tenerla encendida.

## Ejecutar

```bash
npm install
npm start      # http://localhost:4200
npm test       # pruebas de los guards (necesita Chrome)
```

## Estructura

```
src/app/
├── api.config.ts  URL base de la API
├── models/        Interfaces con los mismos nombres de campo que las entidades y DTOs de Spring Boot
├── service/       Solo peticiones HTTP (HttpClient), devuelven Observables. AuthService guarda el usuario
├── guards/        authGuard, rolGuard e invitadoGuard (control de acceso por rol)
├── utils/         mensajeError: texto para mostrar cuando falla una petición
├── components/    Piezas compartidas (navbar, avatares, badges, campos de formulario, lista de tratamientos…)
└── pages/         Una carpeta por página
```

## Patrones RxJS

- Formularios: `form.events` → `filter(FormSubmittedEvent)` → `switchMap(petición)` con `catchError` adentro y un
  único `subscribe` (login, dueños, mascotas, tratamientos, veterinarios).
- Búsquedas: `debounceTime` + `distinctUntilChanged` + `switchMap` (tablas de dueños, mascotas y veterinarios).
- Consultas en paralelo con `forkJoin` (detalle de dueño, formularios) y anidadas con `switchMap`
  (detalle de mascota → su dueño). Acciones en cola con `Subject` + `concatMap` (eliminar, activar/desactivar).
- Todos los flujos se cierran con `takeUntilDestroyed`.

## Rutas

| Ruta | Página | Acceso |
|---|---|---|
| `/` | Landing | Todos |
| `/login` | Ingresar | Sin sesión (con sesión redirige al portal) |
| `/acceso-denegado` | Rol sin permiso | Todos |
| `/cliente`, `/cliente/mascotas`, `/cliente/mascota/:id` | Portal cliente: mis mascotas y su historial | Cliente |
| `/vet`, `/vet/duenos`, `/vet/dueno/new`, `/vet/dueno/update/:cedula`, `/vet/dueno/:cedula` | CRUD de dueños | Veterinario |
| `/vet/mascotas`, `/vet/mascota/new`, `/vet/mascota/update/:id`, `/vet/mascota/:id` | CRUD de mascotas | Veterinario |
| `/vet/tratamiento/new`, `/vet/pacientes` | Dar tratamiento, Mis pacientes | Veterinario |
| `/admin`, `/admin/dashboard` | Dashboard | Administrador |
| `/admin/veterinarios`, `/admin/veterinario/new`, `/admin/veterinario/update/:cedula` | CRUD de veterinarios | Administrador |

Cada portal usa `canActivate` y `canActivateChild` con `authGuard` y `rolGuard`. La sesión vive solo en memoria:
al recargar (F5) hay que volver a ingresar.
