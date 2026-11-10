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
```

## Pruebas e2e (Cypress)

`cypress/e2e/` tiene un spec por caso del sprint (registro de cliente y mascota; tratamiento y dashboard).
Cypress abre la aplicación real, así que antes hay que tener encendidas la API (`./mvnw spring-boot:run
-Dspring-boot.run.profiles=e2e`, H2 en memoria) y Angular (`npm start`).

```bash
npm run e2e        # headless (Electron)
npm run e2e:open   # con la interfaz de Cypress, para ver cada paso
npm run e2e:ci     # levanta ng serve, corre las pruebas y lo apaga (la API debe estar encendida)
```

- `cypress.config.ts`: `baseUrl` (4200) y `env.apiUrl` (8090), la misma URL de `api.config.ts`.
- `cypress/support/commands.ts`: comando `cy.login(correo, password)`. `utils.ts`: ayudas para comparar los
  números que pintan los pipes es-CO con los de la API.
- Las pruebas leen el estado de la API con `cy.request` (dashboard y medicamentos) antes de actuar y comparan
  lo que muestra la pantalla con lo esperado; nunca dependen de ids fijos (se generan en la BD).
- `cypress/tsconfig.json` tiene sus propios tipos; la carpeta está excluida del `tsconfig.json` raíz para que no
  choque con Jasmine.

## Estructura

```
src/app/
├── api.config.ts  URL base de la API
├── models/        Interfaces con los mismos nombres de campo que las entidades y DTOs de Spring Boot
├── service/       Solo peticiones HTTP (HttpClient), devuelven Observables
├── utils/         mensajeError (texto de error de la API) e injectPortal (portal y cédula de la URL)
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

No hay sesión ni guards: cualquier pantalla se abre solo con su ruta. La cédula del cliente o del veterinario va en
la URL y cada pantalla la lee con `injectPortal()` (`utils/portal.ts`); los enlaces del portal se arman a partir de
esa base. El login solo valida las credenciales y redirige al portal del rol.

| Ruta | Página |
|---|---|
| `/` | Landing |
| `/login` | Ingresar (redirige a `/cliente/<cédula>`, `/vet/<cédula>` o `/admin`) |
| `/cliente/:cliente`, `.../mascotas`, `.../mascota/:id` | Portal cliente: inicio, mis mascotas y su historial |
| `/vet/:veterinario`, `.../duenos`, `.../dueno/new`, `.../dueno/update/:cedula`, `.../dueno/:cedula` | Portal veterinario: CRUD de dueños |
| `/vet/:veterinario/mascotas`, `.../mascota/new`, `.../mascota/update/:id`, `.../mascota/:id` | CRUD de mascotas |
| `/vet/:veterinario/tratamiento/new`, `.../pacientes` | Dar tratamiento, Mis pacientes |
| `/admin`, `/admin/dashboard` | Portal administrador: dashboard |
| `/admin/veterinarios`, `/admin/veterinario/new`, `/admin/veterinario/update/:cedula` | CRUD de veterinarios |
