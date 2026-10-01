# DogTor Angular — Correcciones del profesor — Blueprint

> Generado por The Architect el 2026-10-01 (modo brownfield: cambio sobre un repo existente)
> Shape: frontend SPA existente (Angular standalone, datos en memoria) · `knowledge/shapes/` no aplica a un repo ya construido; el mapa del repo (§1 *Current state*) ocupa su lugar
> Runtime track: Node + npm según el `package-lock.json` del repo · las versiones salen del lockfile, no de `knowledge/runtime-tracks/`
> Emission mode: bundle (`blueprints/correcciones-profe/`)
> Blueprint version: 1
> Versions last verified: 2026-10-01 — ver §11 para la procedencia de cada paquete

Este cambio atiende tres correcciones del profesor sobre el proyecto Angular de DogTor:
**(1)** las relaciones deben guardarse como objetos y no como ids (se hace para `Mascota → Dueno`),
**(2)** las páginas grandes deben dividirse en componentes pequeños con `input()`/`output()`, y
**(3)** la landing debe ser una landing completa (además se retira el botón flotante de WhatsApp).

Todos los comandos de este documento se ejecutan desde `dogtor-angular/` (la carpeta que contiene
`package.json`), en Git Bash. El bundle vive dentro del repo que cambia
(`dogtor-angular/blueprints/correcciones-profe/`) a propósito: así los gates corren contra este repo.
No lo muevas.

---

## 1. Visión general y No-objetivos (Project Overview & Non-Goals)

### Vision

DogTor es la SPA en Angular 19 de una clínica veterinaria de un proyecto de curso: una landing
pública y un CRUD de mascotas con su dueño e historial médico, todo con datos quemados en servicios
(no hay backend). El profesor revisó la entrega y pidió tres correcciones concretas: que `Mascota`
guarde su `Dueno` como objeto (como un `@ManyToOne` de JPA) en vez de un `duenoId` que cada
componente resolvía por su cuenta; que la tabla, el formulario y el detalle de mascota se partan en
componentes pequeños comunicados con `input()`/`output()`; y que la landing, que hoy es solo un
carrusel, tenga las secciones de una landing real. Este blueprint es el plan para hacer esas tres
correcciones sin romper nada de lo que ya funciona.

### Users

| Persona | Qué viene a hacer | Frecuencia |
|---|---|---|
| Profesor del curso | Revisar que las relaciones sean objetos, que haya componentes pequeños con `input()`/`output()` y que la landing esté completa | Una vez (calificación) |
| Estudiante (Samuel, dueño del repo) | Entregar la corrección y explicarla en la sustentación | Durante la entrega |
| Visitante de la landing (simulado) | Ver servicios, planes, equipo y testimonios, y dejar un mensaje de contacto | En cada demo |
| Recepción/veterinario (simulado) | Listar, crear, editar, activar/desactivar y ver el detalle de mascotas | En cada demo |

### Current state

Mapa del repo leído el 2026-10-01:

| Aspecto | Hoy |
|---|---|
| Runtime / track | Node v24.14.1, npm 11.11.0, `package-lock.json` presente |
| Framework | Angular 19.2.25 standalone (sin NgModules), `@angular-devkit/build-angular:application`, zone.js |
| Estilos | Bootstrap 5.3.8 + bootstrap-icons 1.13.1 cargados en `angular.json`; SCSS por componente; paleta en `src/styles.scss` |
| Datos | Servicios `providedIn: 'root'` con arreglos quemados (`MascotaService` 20 mascotas, `DuenoService` 10 dueños, `RegistroMedicoService`, `DrogaService`, `UsuarioService`); F5 recarga la semilla |
| Tests | Ninguno: cero `*.spec.ts`, `skipTests: true` en todos los schematics, ningún runner en uso |
| Lint / formato | Ninguno; solo `.editorconfig` (2 espacios, comillas simples en TS, salto de línea final) |
| Build | `npx ng build` → exit 0 en ~4 s, configuración `production` por defecto, con un WARNING esperado de Bootstrap ("4 rules skipped due to selector errors"); presupuestos: inicial 1 MB/2 MB, `anyComponentStyle` 4 kB/8 kB |
| Compilador | `strict`, `noPropertyAccessFromIndexSignature`, `strictTemplates`, `strictInputAccessModifiers` |
| Rutas | `''` Landing · `mascotas` tabla · `mascota/new` y `mascota/update/:id` formulario · `mascota/:id` detalle · `**` → `''` |
| Git | `dogtor-angular/` está **sin seguimiento** dentro del repo padre `C:/Users/samue/Universitat/Desktop/2630/Web` (rama `feature/init`), cuyo árbol tiene cambios sin commit en `Proyecto-Veterinaria-2.0/` que no se tocan; no hay etiquetas |
| Instrucciones de agente | No existe `CLAUDE.md` ni `AGENTS.md` en el repo |

Módulos que toca el cambio, tal como están hoy:

- `src/app/models/mascota.model.ts` — `duenoId?: number`.
- `src/app/service/mascota.service.ts` — 20 filas semilla con `duenoId: N`; `getMascotasByDueno(duenoId)`.
- `src/app/pages/mascota-table-page/components/mascota-table/` — inyecta `DuenoService` y resuelve el nombre con `getNombreDueno(mascota.duenoId)`; repite `fotoPorDefecto`.
- `src/app/pages/mascota-form/` — HTML de 124 líneas; `FormControl` `duenoId` con `[ngValue]="dueno.id"`; método `campoInvalido()` y marcado de error repetido por campo.
- `src/app/pages/mascota-detail/` — HTML de 91 líneas; inyecta `DuenoService` y resuelve `dueno` por id; llama `getNombreVeterinario()` y `getNombresDrogas()` desde el template; repite `fotoPorDefecto`.
- `src/app/pages/landing/` — solo el carrusel, con las diapositivas escritas dentro del componente.
- `src/app/app.component.html` / `.scss` — botón flotante de WhatsApp y su regla `.whatsapp-btn`.

Convenciones que el cambio respeta: identificadores y comentarios en español con comentarios
cortos que dicen el porqué; comentario `//DI` encima de cada `inject()`; componentes standalone con
`templateUrl` + `styleUrl` e `imports: []` explícito; `input()`/`output()` como en `page-title` y
`mascota-table`; control de flujo `@if`/`@for (track)`/`@empty`; subcomponentes de una sola página en
`src/app/pages/<pagina>/components/<nombre>/` y compartidos en `src/app/components/<nombre>/`.

### Target state

- `Mascota.dueno?: Dueno` es la única relación con el dueño; `duenoId` no existe en `src/app`. El
  objeto es la misma instancia que guarda `DuenoService`, el formulario lo selecciona con
  `[ngValue]` + `compareWith` por id, y ningún componente llama `getDuenoById`.
- Componentes compartidos nuevos en `src/app/components/`: `estado-badge`, `mascota-avatar`,
  `campo-error`, `campo-texto`, `dueno-card`. Componentes locales nuevos: `dueno-select` y
  `foto-preview` (formulario), `mascota-info-card` y `registro-medico-item` (detalle). El HTML del
  formulario queda en ≤ 70 líneas y el del detalle en ≤ 45.
- La landing toma todo de `LandingService` (modelos en `landing.model.ts`) y se compone de
  `hero-carousel`, `section-header`, `service-card`, `plan-card` (con `output()`), `team-card`,
  `testimonio-card`, `cta-banner` y `contacto-form` (con `output()`); su HTML queda en ≤ 60 líneas.
- No hay botón de WhatsApp. Rutas, paleta, navbar, footer, `angular.json` y dependencias quedan
  idénticos a `dogtor-base`.

### Goals — v1 scope

1. `Mascota` guarda y edita su dueño como objeto `Dueno`; ningún componente resuelve ids de dueño.
2. Tabla, formulario y detalle de mascota se arman con componentes pequeños que reciben datos con `input()` y emiten eventos con `output()`.
3. La landing muestra carrusel, servicios, planes (con elección de plan), equipo, testimonios, banner de llamada a la acción y formulario de contacto validado, todo desde `LandingService`.
4. El botón flotante de WhatsApp desaparece.
5. Cada paso deja el build en verde y tiene una etiqueta de git a la que se puede volver sin tocar el trabajo ajeno del repo padre.

### Non-Goals — explicitly out of scope for v1

| Not building | Why not now | Revisit when |
|---|---|---|
| Relaciones como objeto en `RegistroMedico` (`mascotaId`, `veterinarioId`, `drogaIds`), `Dueno.usuarioId`, `Administrador.usuarioId`, `Veterinario.usuarioId` | El usuario decidió limitar esta entrega a `Mascota → Dueno`; las demás tocan más servicios y semillas | El profesor pida el mismo cambio para esas entidades |
| Backend, `HttpClient` o cualquier llamada de red | El proyecto Angular del curso trabaja con datos en memoria por diseño | El curso pida conectar Angular con el backend Spring |
| Dependencias nuevas o actualizaciones de versión | El cambio no las necesita y cualquier upgrade agrega riesgo al build | Una corrección futura requiera una librería concreta |
| Tests, linters o formatters | La convención del repo es sin tests (`skipTests: true`, cero `*.spec.ts`) | El curso exija pruebas unitarias |
| Cambios de rutas (interfaz congelada, §5) | Las rutas actuales ya cubren todas las páginas | Se agregue una página nueva |
| Cambios de paleta en `src/styles.scss` (interfaz congelada, §5) | La identidad visual es la misma del proyecto Spring | El profesor pida un rediseño |
| Cambios en navbar y footer (interfaz congelada, §5) | No forman parte de las correcciones | Se agreguen páginas que deban enlazarse |
| Cambiar el contenido de las semillas existentes (mascotas, dueños, registros, drogas, usuarios) | Las correcciones no lo requieren y la demo depende de esos datos | Haya un backend real |
| Envío real del formulario de contacto | No hay backend ni servicio de correo; solo se muestra un mensaje de éxito | Exista un endpoint de contacto |
| Persistencia más allá de la memoria (localStorage, IndexedDB) | Hoy F5 vuelve a la semilla y así se mantiene | El curso pida persistencia en el cliente |
| `angular.json`, `package.json` y `package-lock.json` (interfaz congelada, §5) | Ni presupuestos ni scripts ni dependencias cambian | Un paso futuro necesite otra configuración |

**The builder must not implement anything in this table**, even if it seems like a small addition
while working on an adjacent step. If a step appears to require a non-goal, that is a blueprint
defect — stop and report it rather than expanding scope.

### Success metrics

| Metric | Target | How measured |
|---|---|---|
| Ocurrencias de `duenoId` en `src/app` | 0 al cerrar el paso 2 | `grep -rn duenoId src/app` (gate del paso 2) |
| Líneas de los HTML de página partidos | formulario ≤ 70, detalle ≤ 45, landing ≤ 60 al cerrar los pasos 4, 5 y 9 | `wc -l` en los gates de esos pasos |
| Build | exit 0 y ningún aviso de presupuesto al cerrar el paso 10 | primera línea del gate de §20.1 |
| Interfaces congeladas | 0 diferencias frente a `dogtor-base` | `git diff --quiet dogtor-base -- …` en §20.1 |

---

## 2. Stack tecnológico (Tech Stack)

**Runtime track: Node + npm con el lockfile del repo.** Esta tabla nombra elecciones, no versiones;
toda versión vive en §11.

| Layer | Choice | Why this, over what |
|---|---|---|
| Language / runtime | TypeScript en modo `strict` sobre Node (solo para compilar) | Es lo que el repo ya usa; cambiarlo no es parte de la corrección |
| Framework | Angular standalone con `input()`/`output()` de señales | El profesor pide componentes con `input()`/`output()`; los decoradores `@Input`/`@Output` serían el estilo viejo que el repo ya no usa |
| Styling | Bootstrap 5.3 + bootstrap-icons + SCSS mínimo por componente | Ya están en `angular.json`; utilidades de Bootstrap primero mantienen cada `.scss` por debajo del presupuesto de 4 kB |
| Component layer | Componentes propios, sin librería de UI | ng-bootstrap/Angular Material serían dependencias nuevas (No-objetivo) |
| Database | NOT APPLICABLE — no hay base de datos: arreglos en servicios `providedIn: 'root'` | Convención del proyecto Angular del curso |
| ORM / data access | NOT APPLICABLE — los servicios exponen getters sobre arreglos | Sin base de datos no hay capa de acceso |
| Auth | NOT APPLICABLE — la app Angular no tiene login | El login vive en el proyecto Spring, fuera de este repo |
| Background work | NOT APPLICABLE — no hay trabajos en segundo plano | — |
| Payments | NOT APPLICABLE — los planes se muestran, no se cobran | — |
| File storage | NOT APPLICABLE — las fotos son URLs externas ya existentes | — |
| Email / notifications | NOT APPLICABLE — el formulario de contacto no envía nada | No-objetivo explícito |
| Hosting | NOT APPLICABLE — solo `npx ng serve` local para la demo | No hay despliegue en el curso |
| Package manager | npm con `npm ci` | El repo trae `package-lock.json`; `npm ci` instala exactamente lo bloqueado y nunca lo modifica |

### Compatibility check

Checked against `knowledge/stack-compatibility.md` — no known-bad combinations. El archivo no lista
combinaciones de Angular ni de Bootstrap, y este cambio no agrega ni actualiza ninguna dependencia:
la combinación instalada es la misma con la que `npx ng build` ya termina con exit 0.

---

## 3. Estructura de directorios (Directory Structure)

```
dogtor-angular/                              # raíz del proyecto: aquí corren todos los comandos
  package.json                               # existe; no cambia (interfaz congelada)
  package-lock.json                          # existe; no cambia; npm ci lo usa
  angular.json                               # existe; no cambia; outputPath dist/dogtor-angular
  tsconfig.json / tsconfig.app.json          # existen; tsconfig.app.json solo compila el grafo de src/main.ts
  .gitignore                                 # existe; ignora /node_modules, /dist, /.angular/cache
  CLAUDE.md                                  # NUEVO — llega con la copia de workspace/ (§19.1)
  AGENTS.md                                  # NUEVO — llega con la copia de workspace/ (§19.2)
  .claude/
    settings.json                            # NUEVO — permisos del builder (§19.3)
    skills/verificar-cambio/SKILL.md         # NUEVO — corre el gate de §20.1 (§19.4)
    rules/angular-componentes.md             # NUEVO — convenciones para src/app/** (§19.5)
  blueprints/correcciones-profe/             # este bundle; nunca lo compila ni lo recorre ningún gate
  dist/dogtor-angular/browser/               # salida de npx ng build (ignorada por git)
  src/
    styles.scss                              # existe; no cambia (paleta)
    app/
      app.component.html / .scss             # paso 6: se quita el botón de WhatsApp
      app.routes.ts                          # no cambia (interfaz congelada)
      models/
        mascota.model.ts                     # pasos 1–2: dueno?: Dueno, sin duenoId
        landing.model.ts                     # NUEVO paso 6: Slide, Servicio, Plan, MiembroEquipo, Testimonio, DatosContacto
      service/
        mascota.service.ts                   # pasos 1–2: semilla con dueno objeto
        landing.service.ts                   # NUEVO paso 6: todos los datos de la landing
      components/                            # compartidos (aquí ya viven navbar y footer)
        estado-badge/                        # NUEVO paso 3: badge Activa/Inactiva
        mascota-avatar/                      # NUEVO paso 3: foto redonda con imagen por defecto
        campo-error/                         # NUEVO paso 4: mensaje del primer error del control
        campo-texto/                         # NUEVO paso 4: label + input + campo-error
        dueno-card/                          # NUEVO paso 5: datos del dueño o "Sin dueño asignado"
      pages/
        mascota-table-page/components/mascota-table/   # pasos 1 y 3
        mascota-form/                        # pasos 2 y 4 (HTML ≤ 70 líneas)
          components/dueno-select/           # NUEVO paso 4: <select> con compareWith
          components/foto-preview/           # NUEVO paso 4: vista previa de 96 px
        mascota-detail/                      # pasos 1, 3 y 5 (HTML ≤ 45 líneas)
          components/mascota-info-card/      # NUEVO paso 5: tarjeta con avatar, badge, datos, dueño y Editar
          components/registro-medico-item/   # NUEVO paso 5: un registro médico ya resuelto
        landing/                             # pasos 6–9 (HTML ≤ 60 líneas)
          components/hero-carousel/          # NUEVO paso 6: el carrusel actual, sin cambios de comportamiento
          components/section-header/         # NUEVO paso 7: título, subtítulo y ancla opcional
          components/service-card/           # NUEVO paso 7
          components/plan-card/              # NUEVO paso 7: output planElegido
          components/team-card/              # NUEVO paso 8
          components/testimonio-card/        # NUEVO paso 8
          components/cta-banner/             # NUEVO paso 8: routerLink a /mascotas
          components/contacto-form/          # NUEVO paso 9: output enviado
```

Cada carpeta de componente contiene exactamente `<nombre>.component.ts`, `.html` y `.scss`.

**Boundary rules**
- Las páginas (`src/app/pages/<p>/<p>.component.ts`) son las únicas que inyectan servicios.
- Los componentes de `src/app/pages/<p>/components/` y de `src/app/components/` son
  presentacionales: reciben datos por `input()` y avisan por `output()`; no inyectan servicios.
- Ninguna página importa de otra página; lo compartido sube a `src/app/components/`.
- Los imports entre módulos son relativos y sin extensión. Es una convención de resolución: su
  reconciliación contra cada contexto que carga módulos está en §19.6, *Resolution convention matrix*.

El único path de salida que aparece aquí, `dist/dogtor-angular/browser/`, sale literal de §19.6,
*Cross-artifact value reconciliation*. Origen de cada archivo nuevo: lo escribe el paso indicado
(lista **Archivos** de ese paso) o llega con la copia de `workspace/` de §10.

### Delta

| Tipo | Archivos |
|---|---|
| Agregados (paso) | `models/landing.model.ts` (6) · `service/landing.service.ts` (6) · 5 carpetas en `components/` (3, 4, 5) · 4 carpetas locales del formulario y del detalle (4, 5) · 8 carpetas locales de la landing (6–9) · `CLAUDE.md`, `AGENTS.md`, `.claude/**` (Bootstrap §10) |
| Modificados | `models/mascota.model.ts` · `service/mascota.service.ts` · `mascota-table.component.{ts,html,scss}` · `mascota-form.component.{ts,html,scss}` · `mascota-detail.component.{ts,html,scss}` · `landing.component.{ts,html,scss}` · `app.component.{html,scss}` |
| Eliminados | Ningún archivo. Se eliminan bloques dentro de archivos: el botón y la regla de WhatsApp, `getNombreDueno`, `campoInvalido`, las reglas `.foto` y `.preview` de las páginas, las diapositivas dentro de `landing.component.ts` |

---

## 4. Modelo de datos (Data Model)

### Entities

**Mascota** — paciente de la clínica; vive en el arreglo de `MascotaService` hasta que se elimina o se recarga la página.

| Field | Type | Constraints | Notes |
|---|---|---|---|
| `id` | `number` | PK; lo asigna `addMascota` (máximo + 1) | No se reutiliza aunque se eliminen mascotas |
| `nombre` | `string` | requerido, 2–50 letras | Validado en el formulario |
| `raza` | `string` opcional | ≤ 50 | — |
| `edad` | `string` opcional | ≤ 30 | Texto libre ("2 meses") |
| `fotoUrl` | `string` opcional | debe empezar por `http://` o `https://` | Sin foto se usa la imagen por defecto de `mascota-avatar` |
| `vacunas` | `string` opcional | ≤ 100 | — |
| `activa` | `boolean` | requerido | Al editar se conserva el valor actual |
| `dueno` | `Dueno` opcional | requerido en el formulario | **Objeto**, misma instancia que guarda `DuenoService` (`@ManyToOne`) |

**Dueno** — cliente dueño de mascotas; sin cambios (`id`, `nombre`, `telefono?`, `direccion?`, `fechaCreacion?`, `fechaActualizacion?`, `usuarioId?`).

**RegistroMedico** — historial clínico; sin cambios (`mascotaId`, `veterinarioId?`, `drogaIds` siguen como ids, ver No-objetivos).

**Modelos de la landing** (no son entidades de Spring; son contenido): `Slide`, `Servicio`, `Plan`, `MiembroEquipo`, `Testimonio`, `DatosContacto`. Su definición completa está en el bloque *Schema*.

### Relationships

- `Mascota —(N:1)→ Dueno` vía el objeto `Mascota.dueno`. Borrar una mascota no toca al dueño; los dueños no se borran en esta app.
- `RegistroMedico —(N:1)→ Mascota` vía `mascotaId` (sin cambios). Borrar una mascota borra sus registros (`MascotaService.deleteMascota` llama `deleteRegistrosByMascota`, sin cambios).
- `RegistroMedico —(N:1)→ Usuario` (veterinario) y `—(N:M)→ Droga` vía ids (sin cambios).

### Indexes

| Table | Index | Why |
|---|---|---|
| NOT APPLICABLE | — | No hay base de datos; las búsquedas son `find`/`filter` sobre arreglos de ≤ 20 elementos |

### Schema

```ts
// src/app/models/mascota.model.ts (estado final, paso 2)
import { Dueno } from './dueno.model';

// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: string;
  fotoUrl?: string;
  vacunas?: string;
  activa: boolean;
  dueno?: Dueno; // @ManyToOne con Dueno: se guarda el objeto, no el id
}

// src/app/models/landing.model.ts (paso 6) — cuerpo completo en el paso 6 de §9
export interface Slide { img: string; title: string; desc: string; link?: string; btn?: string; }
export interface Servicio { icono: string; titulo: string; descripcion: string; }
export interface Plan { nombre: string; precio: string; periodo: string; beneficios: string[]; destacado: boolean; }
export interface MiembroEquipo { nombre: string; cargo: string; descripcion: string; }
export interface Testimonio { autor: string; mascota: string; texto: string; estrellas: number; }
export interface DatosContacto { direccion: string; telefono: string; correo: string; horario: string; }
```

El bloque anterior es la referencia; los archivos reales se escriben con el cuerpo literal de los
pasos 2 y 6 (mismo contenido, con los comentarios de cada campo).

### Migrations

NOT APPLICABLE — no hay base de datos ni herramienta de migraciones. El cambio de forma de
`Mascota` se hace en dos pasos con coexistencia (`duenoId` y `dueno` conviven en el paso 1 y
`duenoId` desaparece en el paso 2) para que el build nunca quede en rojo.

### Seed data

La semilla son los arreglos de los servicios. `MascotaService` conserva sus 20 filas con el mismo
contenido; cada fila cambia `duenoId: N` por `dueno: this.duenoService.getDuenoById(N)` (mismo `N`).
`LandingService` (nuevo) trae 2 diapositivas (las actuales), 6 servicios, 3 planes, 3 miembros del
equipo, 3 testimonios y los datos de contacto. No hay comando de seed: los datos existen al cargar la app.

### Delta

| Cambio | Antes | Después | Paso |
|---|---|---|---|
| `Mascota.duenoId` | `duenoId?: number` | eliminado | 2 |
| `Mascota.dueno` | — | `dueno?: Dueno` (objeto) | 1 |
| Filas semilla de `MascotaService` | `duenoId: N` | `dueno: this.duenoService.getDuenoById(N)` | 1 (agrega) y 2 (quita `duenoId`) |
| `landing.model.ts` | — | 6 interfaces nuevas | 6 |

---

## 5. Diseño de API (API Design)

### Conventions

No hay API HTTP: la app no hace llamadas de red y no se agrega `HttpClient`. La "API" de este
proyecto son sus rutas de frontend, los métodos públicos de los servicios y las entradas/salidas de
los componentes. Envoltorio de respuesta, códigos de error, paginación, idempotencia y rate limits:
NOT APPLICABLE — no existe ningún endpoint. Validación: `Validators` de `@angular/forms` en los
formularios reactivos (mascota y contacto).

### Routes

| Method | Path | Description | Auth | Rate limit |
|---|---|---|---|---|
| — (ruta de frontend) | `/` | Landing | público | n/a |
| — | `/mascotas` | Tabla de mascotas activas e inactivas | público | n/a |
| — | `/mascota/new` | Formulario de alta | público | n/a |
| — | `/mascota/update/:id` | Formulario de edición | público | n/a |
| — | `/mascota/:id` | Detalle con dueño e historial médico | público | n/a |
| — | `**` | Redirige a `/` | público | n/a |

### Critical endpoints — full detail

Los contratos que el resto del cambio usa son las entradas y salidas de los componentes nuevos:

| Componente | Entradas (`input`) | Salidas (`output`) |
|---|---|---|
| `app-estado-badge` | `activa: boolean` (requerida) | — |
| `app-mascota-avatar` | `fotoUrl?: string`, `nombre: string` (requerida), `tamano: number = 56` | — |
| `app-campo-error` | `control: AbstractControl` (requerida), `mensajes: Record<string, string> = {}` | — |
| `app-campo-texto` | `campoId`, `etiqueta` (requeridas), `control: FormControl<string \| null>` (requerida), `placeholder = ''`, `mensajes` | — |
| `app-dueno-select` | `control: FormControl<Dueno \| null>`, `duenos: Dueno[]` (requeridas) | — |
| `app-foto-preview` | `url?: string \| null` | — |
| `app-dueno-card` | `dueno?: Dueno` | — |
| `app-mascota-info-card` | `mascota: Mascota` (requerida) | — |
| `app-registro-medico-item` | `registro: RegistroMedico`, `veterinario: string` (requeridas), `drogas: string[] = []` | — |
| `app-hero-carousel` | `slides: Slide[]` (requerida) | — |
| `app-section-header` | `titulo` (requerida), `subtitulo = ''`, `ancla?: string` | — |
| `app-service-card` | `servicio: Servicio` (requerida) | — |
| `app-plan-card` | `plan: Plan` (requerida) | `planElegido: Plan` |
| `app-team-card` | `miembro: MiembroEquipo` (requerida) | — |
| `app-testimonio-card` | `testimonio: Testimonio` (requerida) | — |
| `app-cta-banner` | `titulo`, `textoBoton`, `ruta` (requeridas), `texto = ''` | — |
| `app-contacto-form` | `datos: DatosContacto` (requerida), `plan?: Plan` | `enviado: { nombre: string; correo: string; mensaje: string }` |

Validaciones del formulario de contacto: `nombre` requerido y mínimo 2 caracteres; `correo`
requerido y con formato de email; `mensaje` requerido y mínimo 10 caracteres. Efecto secundario
del envío válido: emite `enviado`, limpia el formulario y muestra "¡Gracias! Te contactaremos
pronto."; no escribe nada fuera de la memoria del componente.

### Delta

| Método / contrato | Antes | Después | Paso |
|---|---|---|---|
| `MascotaService.getMascotasByDueno` | `(duenoId: number)` filtra `m.duenoId === duenoId` | `(dueno: Dueno)` filtra `m.dueno?.id === dueno.id` (no tiene llamadores) | 2 |
| `MascotaTableComponent.getNombreDueno` | público | eliminado | 1 |
| `MascotaFormComponent.campoInvalido` | público | eliminado | 4 |
| `MascotaDetailComponent.getNombresDrogas` / `getNombreVeterinario` | públicos, llamados desde el template | eliminado / privado, usados una vez en `ngOnInit` | 5 |
| `LandingService` | — | `getSlides`, `getServicios`, `getPlanes`, `getEquipo`, `getTestimonios`, `getContacto` | 6 |

### Interfaces held constant

| Interfaz congelada | Cómo se comprueba |
|---|---|
| Rutas de `src/app/app.routes.ts` | `git diff --quiet dogtor-base -- src/app/app.routes.ts` (§20.1) |
| Paleta y utilidades de `src/styles.scss` | `git diff --quiet dogtor-base -- src/styles.scss` (§20.1) |
| Navbar y footer | `git diff --quiet dogtor-base -- src/app/components/navbar src/app/components/footer` (§20.1) |
| `angular.json`, `package.json`, `package-lock.json` | `git diff --quiet dogtor-base -- angular.json package.json package-lock.json` (§20.1) |
| Métodos públicos de `DuenoService`, `DrogaService`, `UsuarioService`, `RegistroMedicoService` | Ningún paso edita esos archivos (ninguno aparece en las listas **Archivos** de §9) |

Cada interfaz congelada tiene su fila en los No-objetivos de §1.

---

## 6. Arquitectura de frontend (Frontend Architecture)

### Routes

| Route | Page | Data source | Auth |
|---|---|---|---|
| `/` | `LandingComponent` | `LandingService` (estático en memoria) | público |
| `/mascotas` | `MascotaTablePageComponent` | `MascotaService` | público |
| `/mascota/new`, `/mascota/update/:id` | `MascotaFormComponent` | `MascotaService`, `DuenoService` | público |
| `/mascota/:id` | `MascotaDetailComponent` | `MascotaService`, `RegistroMedicoService`, `DrogaService`, `UsuarioService` | público |

### Rendering strategy

Todo es renderizado en el cliente (CSR): builder `@angular-devkit/build-angular:application` sin SSR
ni prerender, igual que hoy. Detección de cambios por zone.js con `provideZoneChangeDetection({ eventCoalescing: true })`, sin `OnPush`. No hay caché ni revalidación.

### Component hierarchy

```
LandingComponent (página; inyecta LandingService)
├── app-hero-carousel [slides]
├── app-section-header × 5 [titulo, subtitulo, ancla]
├── app-service-card × 6 [servicio]
├── app-plan-card × 3 [plan] (planElegido) → elegirPlan() → scroll a #contacto
├── app-team-card × 3 [miembro]
├── app-testimonio-card × 3 [testimonio]
├── app-cta-banner [titulo, texto, textoBoton, ruta]
└── app-contacto-form [datos, plan] (enviado) → contactoEnviado()
    ├── app-campo-texto × 2 → app-campo-error
    └── app-campo-error (mensaje)

MascotaFormComponent (página; inyecta MascotaService, DuenoService)
├── app-campo-texto × 5 [campoId, etiqueta, control, placeholder, mensajes] → app-campo-error
├── app-foto-preview [url]
└── app-dueno-select [control, duenos] → app-campo-error

MascotaDetailComponent (página; resuelve nombres una vez en ngOnInit)
├── app-mascota-info-card [mascota]
│   ├── app-mascota-avatar [fotoUrl, nombre, tamano=180]
│   ├── app-estado-badge [activa]
│   └── app-dueno-card [dueno]
└── app-registro-medico-item × N [registro, veterinario, drogas]
```

### State management

Estado del servidor: no existe. Datos de dominio: arreglos en servicios singleton. Estado de UI:
campos simples en las páginas (`planSeleccionado`, `registros`, `activeSlide` en el carrusel,
`enviadoConExito` en el formulario de contacto). Formularios: `FormGroup` tipados de
`@angular/forms`. No se agrega ningún store global; la comunicación es padre → hijo con `input()`
e hijo → padre con `output()`. Las entradas de los hijos son señales; los valores que se pintan
como listas se calculan una vez (nunca un método de template que devuelva un arreglo nuevo).

### Loading, empty, and error states

| Superficie | Cargando | Vacío | Error |
|---|---|---|---|
| Tabla de mascotas | n/a (datos síncronos) | "No hay mascotas en esta lista." (sin cambios) | n/a |
| Detalle | n/a | "Esta mascota aún no tiene registros médicos." (`@empty`) · dueño ausente → "Sin dueño asignado" | id inexistente → "No se encontró la mascota con id N." (sin cambios) |
| Formulario de mascota | n/a | — | id inexistente → "No se encontró la mascota." · validación por campo con `app-campo-error` |
| Formulario de contacto | n/a | — | validación por campo; éxito → "¡Gracias! Te contactaremos pronto." |
| Imágenes | n/a | foto de mascota ausente → imagen por defecto; equipo y testimonios usan iconos (sin URLs externas nuevas) | — |

---

## 7. Sistema de diseño (Design System)

Se conserva el sistema actual: la paleta de `src/styles.scss` (congelada) y los componentes de
Bootstrap 5.3. No hay modo oscuro en la app.

### Colors

| Token | Light | Dark | Usage |
|---|---|---|---|
| `--primary` → `--dogtor-primary` | #2a9d8f | — (sin modo oscuro) | `btn-dogtor`, `text-dogtor`, iconos de servicios y planes, borde del plan destacado |
| `--primary-fg` | #ffffff | — | texto de `btn-dogtor` |
| `--background` → `--dogtor-bglight` | #fafafa | — | fondo de página |
| `--surface` | #ffffff (`bg-white`, `.card`) | — | tarjetas y secciones alternas |
| `--border` | #dee2e6 (borde por defecto de Bootstrap) | — | inputs, tarjetas, avatar |
| `--fg` → `--dogtor-textdark` | #264653 | — | texto, títulos, `bg-dogtor-dark` |
| `--fg-muted` | #6c757d (`text-muted`) | — | subtítulos y descripciones |
| `--destructive` | #dc3545 (`btn-danger`, `invalid-feedback`) | — | eliminar, errores de validación |
| `--success` | #198754 (`text-bg-success`, `alert-success`) | — | badge Activa, mensaje de contacto enviado |

Acentos existentes: `--dogtor-secondary` #f4a261 (`btn-dogtor-secondary`) y `--dogtor-accent`
#e9c46a (`btn-dogtor-accent`, estrellas de testimonios).

**Contrast:** medido con la fórmula de WCAG 2.2: #264653 sobre #fafafa = **9.66:1** (pasa AA);
#6c757d sobre #ffffff = **4.69:1** (pasa AA); #ffffff sobre #2a9d8f = **3.32:1** (pasa solo para
texto grande y bordes de UI); #ffffff sobre #f4a261 = **2.06:1** (no pasa). Los dos últimos son
colores de la paleta existente, que está congelada por el No-objetivo de `src/styles.scss`; este
cambio no agrega ningún par nuevo de texto sobre esos fondos fuera de botones con `fw-600`. Queda
registrado en §20.2 y §20.4.

### Typography

| Role | Family | Size / line-height | Weight | Tracking |
|---|---|---|---|---|
| Display | pila del sistema de Bootstrap (`system-ui, -apple-system, "Segoe UI", Roboto, …`) | `display-4` / `display-6` de Bootstrap | 700 (`fw-bold`) | normal |
| Heading | misma pila | `h2`–`h6` de Bootstrap (`h4`, `h5`, `h6` como clases) | 700 | normal |
| Body | misma pila | 1rem / 1.5 | 400 | normal |
| Mono | `SFMono-Regular, Menlo, Monaco, Consolas, …` (Bootstrap) | 0.875em | 400 | normal |

**Font loading:** no se cargan fuentes web; se usa la pila del sistema de Bootstrap (cero peticiones).

### Spacing, radius, elevation

- Spacing scale: la de Bootstrap (`$spacer` = 1rem; utilidades 0, 0.25, 0.5, 1, 1.5, 3 rem). Secciones de la landing con `py-5`.
- Radius: tarjetas `rounded-4` (1rem); avatar y badges `rounded-circle`/por defecto; vista previa `rounded-3`.
- Shadows: `shadow-sm` en tarjetas de contenido, `shadow` en las tarjetas principales de formulario y detalle (valores de Bootstrap).
- Max content width: `.container` de Bootstrap · Breakpoints: los de Bootstrap (576, 768, 992, 1200, 1400 px); grillas `col-md-6 col-lg-4`.

### Motion

Solo el fundido del carrusel (0.5 s, sin cambios) y el `scroll-behavior: smooth` existente de
`styles.scss`, más `scrollIntoView({ behavior: 'smooth' })` al elegir un plan. No se agregan
animaciones nuevas. `prefers-reduced-motion` no se maneja hoy en `styles.scss` (congelado);
queda en §20.4.

### Component style

Tarjetas blancas sin borde, esquinas de 1rem y sombra suave sobre fondo #fafafa; acentos en
#2a9d8f; iconos de bootstrap-icons en lugar de fotos para todo contenido nuevo. Un componente nuevo
pertenece al sistema si se arma con clases de Bootstrap y las utilidades `*-dogtor` sin colores
literales nuevos.

---

## 8. Autenticación y autorización (Authentication & Authorization)

NOT APPLICABLE — la app Angular no tiene cuentas ni login (la autenticación vive en el proyecto
Spring, fuera de este repo) y este cambio no la agrega. Ningún paso de §9 depende de esta sección.

---

## 9. BUILD ORDER

### Reglas de un paso en este repo

Se aplican las reglas de la plantilla (una sesión por paso; `Do` → `Done when` → `Verify` →
`Checkpoint`; criterios EARS observables; `Verify` literal que termina con exit 0 cuando el paso
está bien; ningún gate depende del commit de su propio paso; ningún paso rompe el gate de uno
anterior) con estas adaptaciones de brownfield, todas obligatorias:

1. **Git con pathspec, siempre.** `dogtor-angular/` vive dentro de un repo padre con trabajo ajeno
   sin commit. Cada Checkpoint hace `git add -A -- src blueprints` y `git commit … -- src blueprints`
   (commit con pathspec = semántica `--only`: no arrastra nada que esté preparado fuera de esas
   rutas). Las etiquetas se llaman `dogtor-sNN` (no `step-NN-slug`) porque el repo padre es
   compartido; la base es `dogtor-base`.
2. **Rollback sin `git reset --hard`.** Volver de un paso N fallido es
   `git restore --source=<etiqueta del paso N-1> --staged --worktree -- src && git clean -fd -- src`
   (para el paso 1 la fuente es `dogtor-base`). `git reset --hard` está prohibido: destruiría los
   cambios sin commit de `Proyecto-Veterinaria-2.0/`.
3. **Unidad de archivos.** Un componente standalone son 3 archivos (`.ts`, `.html`, `.scss`) creados
   juntos; en `tasks.json` se listan como un glob por carpeta y cuentan como una unidad frente al
   límite de ~5. Ningún paso pasa de 5 unidades.
4. **Gates persistentes.** Cada gate está escrito para seguir pasando después de los pasos
   siguientes (por ejemplo, el del paso 2 busca `compararDuenos` en toda `src/app/pages/mascota-form/`
   porque el paso 4 lo mueve a `dueno-select`). El paso 10 los re-ejecuta todos.
5. **Sin entry point nuevo.** El cambio no crea ejecutables, manifiestos ni endpoints: `main.ts`,
   `index.html`, `angular.json` y `package.json` quedan iguales (el paso 10 lo comprueba contra
   `dogtor-base`). El build de producción compila AOT todas las plantillas con `strictTemplates`
   en cada paso, y el paso 10 comprueba que la salida quedó en `dist/dogtor-angular/browser/index.html`.
   La ejecución en el navegador se recorre con el checklist visual manual de §20.1, que no es un gate.
6. **Contenido literal.** Cada paso da el cuerpo completo de cada archivo que crea o reemplaza. No se
   inventa nada: si un archivo no aparece en la lista **Archivos** de un paso, ese paso no lo toca.

### One step, one unit — the counting rule

Un paso de §9 = una tarea de `tasks.json` = un bloque de tarea en un epic. Este cambio tiene
**10 pasos** y **10 tareas**. La plantilla pide epics de 5–9 pasos (para 10 pasos, exactamente 2
epics); este bundle usa **3 epics** (pasos 1–2, 3–5 y 6–10) porque el usuario aprobó explícitamente
un epic por cada corrección del profesor. La desviación está registrada en §20.3; no cambia el
orden ni el contenido de ningún paso.

### Step map

| # | Paso | Tarea | Depende de | Toca | Gate principal |
|---|---|---|---|---|---|
| 1 | Mascota guarda el objeto Dueno (coexiste con duenoId) | `E1-T1` | — (Bootstrap §10) | `src/app/models/mascota.model.ts` · `src/app/service/mascota.service.ts` · `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts` · `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html` · `src/app/pages/mascota-detail/mascota-detail.component.ts` | `npx ng build` + 5 gates estáticos |
| 2 | Formulario usa el objeto Dueno y se elimina duenoId | `E1-T2` | 1 | `src/app/models/mascota.model.ts` · `src/app/service/mascota.service.ts` · `src/app/pages/mascota-form/mascota-form.component.ts` · `src/app/pages/mascota-form/mascota-form.component.html` | `npx ng build` + 5 gates estáticos |
| 3 | Componentes compartidos estado-badge y mascota-avatar | `E2-T1` | 2 | `src/app/components/estado-badge/*` · `src/app/components/mascota-avatar/*` · `src/app/pages/mascota-table-page/components/mascota-table/*` · `src/app/pages/mascota-detail/mascota-detail.component.*` | `npx ng build` + 6 gates estáticos |
| 4 | Formulario de mascota dividido en componentes de campo | `E2-T2` | 3 | `src/app/components/campo-error/*` · `src/app/components/campo-texto/*` · `src/app/pages/mascota-form/components/dueno-select/*` · `src/app/pages/mascota-form/components/foto-preview/*` · `src/app/pages/mascota-form/mascota-form.component.*` | `npx ng build` + 6 gates estáticos |
| 5 | Detalle de mascota dividido en tarjetas presentacionales | `E2-T3` | 4 | `src/app/components/dueno-card/*` · `src/app/pages/mascota-detail/components/mascota-info-card/*` · `src/app/pages/mascota-detail/components/registro-medico-item/*` · `src/app/pages/mascota-detail/mascota-detail.component.*` | `npx ng build` + 5 gates estáticos |
| 6 | LandingService, hero-carousel y fuera el botón de WhatsApp | `E3-T1` | 5 | `src/app/models/landing.model.ts` · `src/app/service/landing.service.ts` · `src/app/pages/landing/components/hero-carousel/*` · `src/app/pages/landing/landing.component.*` · `src/app/app.component.*` | `npx ng build` + 5 gates estáticos |
| 7 | Secciones de servicios y planes con service-card y plan-card | `E3-T2` | 6 | `src/app/pages/landing/components/section-header/*` · `src/app/pages/landing/components/service-card/*` · `src/app/pages/landing/components/plan-card/*` · `src/app/pages/landing/landing.component.*` | `npx ng build` + 5 gates estáticos |
| 8 | Secciones de equipo, testimonios y banner a la acción | `E3-T3` | 7 | `src/app/pages/landing/components/team-card/*` · `src/app/pages/landing/components/testimonio-card/*` · `src/app/pages/landing/components/cta-banner/*` · `src/app/pages/landing/landing.component.*` | `npx ng build` + 4 gates estáticos |
| 9 | Formulario de contacto con plan precargado (sin envío real) | `E3-T4` | 8 | `src/app/pages/landing/components/contacto-form/*` · `src/app/pages/landing/landing.component.*` | `npx ng build` + 5 gates estáticos |
| 10 | Gate final de integración de las correcciones | `E3-T5` | 9 | `blueprints/correcciones-profe/tasks.json` | arreglo `verify` de E3-T5 (= §20.1) |


---

#### Paso 1 — Mascota guarda el objeto Dueno (coexiste con duenoId)

**Objetivo:** Después de este paso cada `Mascota` lleva su `dueno` como objeto y ni la tabla ni el detalle resuelven ids de dueño.  
**Tarea:** `E1-T1` (epic `01-mascota-dueno-objeto`) · **Depende de:** §10 Bootstrap · **Prioridad:** p0

**Do**

Primer paso del cambio incremental de `duenoId` a `dueno`. `Mascota` gana `dueno?: Dueno` y **conserva temporalmente** `duenoId?: number` (lo sigue usando el formulario hasta el paso 2), así el build nunca queda en rojo.

`MascotaService` inyecta `DuenoService` **antes** de declarar `mascotaArray` (los inicializadores de campos se ejecutan en orden de declaración) y cada una de las filas semilla pasa a llevar `dueno: this.duenoService.getDuenoById(N)` con el mismo `N` que su `duenoId`. Así el objeto `Dueno` de la mascota es **la misma instancia** que guarda `DuenoService`, que es lo que el `compareWith` del paso 2 necesita. Resolver el id dentro del seed del servicio es aceptable: la queja del profesor era que los *componentes* resolvían ids.

La tabla deja de inyectar `DuenoService` y pierde `getNombreDueno`; muestra `mascota.dueno?.nombre`. El detalle deja de inyectar `DuenoService`: su propiedad `dueno` pasa a ser un *getter* que lee `this.mascota?.dueno`, por eso su HTML no cambia en este paso (se rehace en el paso 5).

Estado transitorio conocido: entre este paso y el paso 2, una mascota guardada desde el formulario queda con `duenoId` pero sin `dueno`, y la tabla muestra "—" para ella. Se corrige en el paso 2; no hay persistencia (F5 recarga la semilla).

##### Archivos

- `src/app/models/mascota.model.ts` — REEMPLAZAR
- `src/app/service/mascota.service.ts` — REEMPLAZAR
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.ts` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/models/mascota.model.ts` (REEMPLAZAR):

```ts
import { Dueno } from './dueno.model';

// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: string;
  fotoUrl?: string;
  vacunas?: string;
  activa: boolean;
  duenoId?: number; // TEMPORAL: convive con "dueno" hasta que el formulario use el objeto; luego se elimina
  dueno?: Dueno; // @ManyToOne con Dueno: se guarda el objeto, no el id
}
```

`src/app/service/mascota.service.ts` (REEMPLAZAR):

```ts
import { Injectable, inject } from '@angular/core';
import { Mascota } from '../models/mascota.model';
import { RegistroMedicoService } from './registro-medico.service';
import { DuenoService } from './dueno.service';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  //DI
  private registroMedicoService = inject(RegistroMedicoService);
  // Debe ir ANTES de mascotaArray: los inicializadores de campos se ejecutan en orden
  // y el arreglo de abajo ya usa duenoService para guardar el objeto Dueno
  private duenoService = inject(DuenoService);

  // "Base de datos" quemada: el servicio es un singleton, así que los cambios
  // se mantienen mientras navegas, pero al recargar (F5) vuelven estos datos.
  private mascotaArray: Mascota[] = [
    { id: 1, nombre: 'Max', raza: 'Golden Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 1, dueno: this.duenoService.getDuenoById(1) },
    { id: 2, nombre: 'Luna', raza: 'Gato Siamés', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 1, dueno: this.duenoService.getDuenoById(1) },
    { id: 3, nombre: 'Rocky', raza: 'Bulldog Francés', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 2, dueno: this.duenoService.getDuenoById(2) },
    { id: 4, nombre: 'Bella', raza: 'Gato Persa', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 2, dueno: this.duenoService.getDuenoById(2) },
    { id: 5, nombre: 'Toby', raza: 'Poodle', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 3, dueno: this.duenoService.getDuenoById(3) },
    { id: 6, nombre: 'Oreo', raza: 'Gato Naranja Común', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 3, dueno: this.duenoService.getDuenoById(3) },
    { id: 7, nombre: 'Simba', raza: 'Golden Retriever', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1591160690555-5debfba289f0?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 4, dueno: this.duenoService.getDuenoById(4) },
    { id: 8, nombre: 'Felix', raza: 'Gato Común Europeo', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1543852786-1cf6624b9987?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: false, duenoId: 4, dueno: this.duenoService.getDuenoById(4) },
    { id: 9, nombre: 'Milo', raza: 'Labrador Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1611003228941-98852ba62227?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 5, dueno: this.duenoService.getDuenoById(5) },
    { id: 10, nombre: 'Pelusa', raza: 'Gato Negro', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1546527868-ccb7ee7dfa6a?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 5, dueno: this.duenoService.getDuenoById(5) },
    { id: 11, nombre: 'Thor', raza: 'Husky Siberiano', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1568572933382-74d440642117?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, duenoId: 6, dueno: this.duenoService.getDuenoById(6) },
    { id: 12, nombre: 'Michi', raza: 'Gato Atigrado', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 6, dueno: this.duenoService.getDuenoById(6) },
    { id: 13, nombre: 'Buddy', raza: 'Pastor Alemán', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 7, dueno: this.duenoService.getDuenoById(7) },
    { id: 14, nombre: 'Salem', raza: 'Gato Naranja', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 7, dueno: this.duenoService.getDuenoById(7) },
    { id: 15, nombre: 'Duke', raza: 'Mestizo', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 8, dueno: this.duenoService.getDuenoById(8) },
    { id: 16, nombre: 'Whiskers', raza: 'Ragdoll', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 8, dueno: this.duenoService.getDuenoById(8) },
    { id: 17, nombre: 'Rex', raza: 'Chihuahua', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 9, dueno: this.duenoService.getDuenoById(9) },
    { id: 18, nombre: 'Nala', raza: 'Gato Mestizo', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1615789591457-74a63395c990?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 9, dueno: this.duenoService.getDuenoById(9) },
    { id: 19, nombre: 'Zeus', raza: 'Pug', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 10, dueno: this.duenoService.getDuenoById(10) },
    { id: 20, nombre: 'Simona', raza: 'Abisinio', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1571566882372-1598d88abd90?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, duenoId: 10, dueno: this.duenoService.getDuenoById(10) },
  ];

  getMascotas() {
    return this.mascotaArray;
  }

  getMascotasActivas() {
    return this.mascotaArray.filter((m) => m.activa);
  }

  getMascotasInactivas() {
    return this.mascotaArray.filter((m) => !m.activa);
  }

  getMascotaById(id: number) {
    return this.mascotaArray.find((m) => m.id === id);
  }

  getMascotasByDueno(duenoId: number) {
    return this.mascotaArray.filter((m) => m.duenoId === duenoId);
  }

  addMascota(mascota: Mascota) {
    // Siguiente id = máximo actual + 1 (no se repite aunque se eliminen mascotas)
    mascota.id = Math.max(0, ...this.mascotaArray.map((m) => m.id)) + 1;
    this.mascotaArray.push(mascota);
  }

  updateMascota(id: number, mascota: Mascota) {
    const index = this.mascotaArray.findIndex((m) => m.id === id);
    if (index !== -1) {
      this.mascotaArray[index] = { ...mascota, id };
    }
  }

  desactivarMascota(mascota: Mascota) {
    mascota.activa = false;
  }

  activarMascota(mascota: Mascota) {
    mascota.activa = true;
  }

  deleteMascota(mascota: Mascota) {
    this.registroMedicoService.deleteRegistrosByMascota(mascota.id);
    this.mascotaArray = this.mascotaArray.filter((m) => m.id !== mascota.id);
  }
}
```

`src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts` (REEMPLAZAR):

```ts
import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-mascota-table',
  imports: [RouterLink],
  templateUrl: './mascota-table.component.html',
  styleUrl: './mascota-table.component.scss',
})
export class MascotaTableComponent {
  //DI
  router = inject(Router);

  // Entradas: datos que manda el componente padre
  mascotaArray = input<Mascota[]>([]);
  areMascotasActivas = input<boolean>(true);

  // Salidas: eventos que se le avisan al componente padre
  estadoCambiado = output<Mascota>();
  mascotaEliminada = output<Mascota>();

  fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';

  verDetalleMascota(mascota: Mascota) {
    this.router.navigate(['/mascota', mascota.id]);
  }

  cambiarEstado(mascota: Mascota) {
    this.estadoCambiado.emit(mascota);
  }

  eliminarMascota(mascota: Mascota) {
    if (confirm(`¿Eliminar a ${mascota.nombre}? También se borrará su historial médico.`)) {
      this.mascotaEliminada.emit(mascota);
    }
  }
}
```

`src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html` (REEMPLAZAR):

```html
@if (mascotaArray().length === 0) {
  <div class="alert alert-light border text-center text-muted">No hay mascotas en esta lista.</div>
} @else {
  <div class="table-responsive bg-white rounded-3 shadow-sm border">
    <table class="table table-hover align-middle mb-0">
      <thead>
        <tr>
          <th scope="col">Foto</th>
          <th scope="col">ID</th>
          <th scope="col">Nombre</th>
          <th scope="col">Raza</th>
          <th scope="col">Edad</th>
          <th scope="col">Vacunas</th>
          <th scope="col">Dueño</th>
          <th scope="col">Acciones</th>
        </tr>
      </thead>
      <tbody>
        @for (mascota of mascotaArray(); track mascota.id) {
          <tr>
            <td>
              <img
                [src]="mascota.fotoUrl || fotoPorDefecto"
                [alt]="mascota.nombre"
                class="foto rounded-circle border"
              />
            </td>
            <td class="text-muted">{{ mascota.id }}</td>
            <td class="fw-bold text-dogtor">{{ mascota.nombre }}</td>
            <td>{{ mascota.raza }}</td>
            <td>{{ mascota.edad }}</td>
            <td>{{ mascota.vacunas }}</td>
            <td>{{ mascota.dueno?.nombre ?? '—' }}</td>
            <td>
              <div class="d-flex flex-wrap gap-2">
                <button type="button" class="btn btn-sm btn-dogtor" (click)="verDetalleMascota(mascota)">
                  <i class="bi bi-eye"></i> Ver
                </button>
                <a [routerLink]="['/mascota/update', mascota.id]" class="btn btn-sm btn-dogtor-accent">
                  <i class="bi bi-pencil"></i> Editar
                </a>
                @if (areMascotasActivas()) {
                  <button type="button" class="btn btn-sm btn-secondary" (click)="cambiarEstado(mascota)">
                    Desactivar
                  </button>
                } @else {
                  <button type="button" class="btn btn-sm btn-success" (click)="cambiarEstado(mascota)">
                    Activar
                  </button>
                }
                <button type="button" class="btn btn-sm btn-danger" (click)="eliminarMascota(mascota)">
                  <i class="bi bi-trash"></i> Eliminar
                </button>
              </div>
            </td>
          </tr>
        }
      </tbody>
    </table>
  </div>
}
```

`src/app/pages/mascota-detail/mascota-detail.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, DatePipe],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  registros: RegistroMedico[] = [];

  fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';

  // El dueño ya viene dentro de la mascota (objeto, no id): no hay que buscarlo por id
  get dueno() {
    return this.mascota?.dueno;
  }

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Cargar su historial
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
      this.registros = this.registroMedicoService.getRegistrosByMascota(this.mascotaId);
    }
  }

  getNombreVeterinario(veterinarioId?: number) {
    if (veterinarioId === undefined) {
      return 'Sin asignar';
    }
    return this.usuarioService.getUsuarioById(veterinarioId)?.nombre ?? 'Sin asignar';
  }

  getNombresDrogas(drogaIds: number[]) {
    return drogaIds
      .map((id) => this.drogaService.getDrogaById(id)?.nombre)
      .filter((nombre) => !!nombre)
      .join(', ');
  }
}
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se lee `src/app/models/mascota.model.ts` THE SYSTEM SHALL contener la línea de campo `dueno?: Dueno;` y el import de `Dueno` desde `./dueno.model`.
- [ ] WHEN se lee `src/app/service/mascota.service.ts` THE SYSTEM SHALL declarar `private duenoService = inject(DuenoService);` en una línea anterior a `private mascotaArray: Mascota[] = [`.
- [ ] WHEN se comparan las filas semilla de `src/app/service/mascota.service.ts` con las de la etiqueta `dogtor-base` THE SYSTEM SHALL mostrar, fila por fila y en el mismo orden, `dueno: this.duenoService.getDuenoById(N) }` con el mismo `N` que tenía `duenoId: N }`.
- [ ] WHEN se buscan `DuenoService`, `getDuenoById` y `getNombreDueno` en `mascota-table.component.ts`, `mascota-table.component.html` y `mascota-detail.component.ts` THE SYSTEM SHALL encontrar cero coincidencias.
- [ ] WHEN se lee `mascota-table.component.html` THE SYSTEM SHALL mostrar el dueño con `{{ mascota.dueno?.nombre ?? '—' }}`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/models/mascota.model.ts && grep -qF 'dueno?: Dueno;' src/app/models/mascota.model.ts && grep -qF "import { Dueno } from './dueno.model';" src/app/models/mascota.model.ts  # expect: exit 0
test "$(grep -n 'private duenoService = inject(DuenoService);' src/app/service/mascota.service.ts | cut -d: -f1)" -lt "$(grep -n 'private mascotaArray: Mascota\[\] = \[' src/app/service/mascota.service.ts | cut -d: -f1)"  # expect: exit 0 (duenoService se declara antes del arreglo)
a="$(git show dogtor-base:./src/app/service/mascota.service.ts | grep -oE 'duenoId: [0-9]+ \}' | grep -oE '[0-9]+')"; b="$(grep -oE 'getDuenoById\([0-9]+\) \}' src/app/service/mascota.service.ts | grep -oE '[0-9]+')"; test -n "$a" && test "$a" = "$b"  # expect: exit 0 (cada fila usa el mismo N que tenía en dogtor-base)
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts && test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && test -f src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -nE 'DuenoService|getDuenoById|getNombreDueno' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0, sin coincidencias
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF "{{ mascota.dueno?.nombre ?? '—' }}" src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E1-T1: Mascota guarda el objeto Dueno (coexiste con duenoId)" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null || git tag dogtor-s01
git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-base`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-base --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 2 — Formulario usa el objeto Dueno y se elimina duenoId

**Objetivo:** Después de este paso `duenoId` no existe en `src/app` y el formulario guarda y precarga el objeto `Dueno`.  
**Tarea:** `E1-T2` (epic `01-mascota-dueno-objeto`) · **Depende de:** paso 1 · **Prioridad:** p0

**Do**

Cierra la coexistencia. El `FormControl` `duenoId` pasa a llamarse `dueno` y guarda un `Dueno | null`; las opciones del `<select>` usan `[ngValue]="dueno"` (el objeto) y el `<select>` recibe `[compareWith]="compararDuenos"` para que, al editar, quede seleccionado el dueño actual aunque se compare por id.

`Mascota` pierde `duenoId`; las filas semilla del servicio pierden `duenoId: N, ` y quedan solo con `dueno: this.duenoService.getDuenoById(N)`. `getMascotasByDueno` recibe ahora el objeto (`dueno: Dueno`) y filtra por `m.dueno?.id === dueno.id`: se renombró el parámetro porque el gate exige cero ocurrencias de `duenoId` en `src/app`, y el método no tiene llamadores hoy.

En el HTML del formulario solo cambia el bloque "Dueño" (el resto queda idéntico; se da el archivo completo para no adivinar).

##### Archivos

- `src/app/models/mascota.model.ts` — REEMPLAZAR
- `src/app/service/mascota.service.ts` — REEMPLAZAR
- `src/app/pages/mascota-form/mascota-form.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-form/mascota-form.component.html` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/models/mascota.model.ts` (REEMPLAZAR):

```ts
import { Dueno } from './dueno.model';

// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: string;
  fotoUrl?: string;
  vacunas?: string;
  activa: boolean;
  dueno?: Dueno; // @ManyToOne con Dueno: se guarda el objeto, no el id
}
```

`src/app/service/mascota.service.ts` (REEMPLAZAR):

```ts
import { Injectable, inject } from '@angular/core';
import { Mascota } from '../models/mascota.model';
import { Dueno } from '../models/dueno.model';
import { RegistroMedicoService } from './registro-medico.service';
import { DuenoService } from './dueno.service';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  //DI
  private registroMedicoService = inject(RegistroMedicoService);
  // Debe ir ANTES de mascotaArray: los inicializadores de campos se ejecutan en orden
  // y el arreglo de abajo ya usa duenoService para guardar el objeto Dueno
  private duenoService = inject(DuenoService);

  // "Base de datos" quemada: el servicio es un singleton, así que los cambios
  // se mantienen mientras navegas, pero al recargar (F5) vuelven estos datos.
  private mascotaArray: Mascota[] = [
    { id: 1, nombre: 'Max', raza: 'Golden Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(1) },
    { id: 2, nombre: 'Luna', raza: 'Gato Siamés', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(1) },
    { id: 3, nombre: 'Rocky', raza: 'Bulldog Francés', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(2) },
    { id: 4, nombre: 'Bella', raza: 'Gato Persa', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(2) },
    { id: 5, nombre: 'Toby', raza: 'Poodle', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(3) },
    { id: 6, nombre: 'Oreo', raza: 'Gato Naranja Común', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(3) },
    { id: 7, nombre: 'Simba', raza: 'Golden Retriever', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1591160690555-5debfba289f0?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(4) },
    { id: 8, nombre: 'Felix', raza: 'Gato Común Europeo', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1543852786-1cf6624b9987?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: false, dueno: this.duenoService.getDuenoById(4) },
    { id: 9, nombre: 'Milo', raza: 'Labrador Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1611003228941-98852ba62227?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(5) },
    { id: 10, nombre: 'Pelusa', raza: 'Gato Negro', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1546527868-ccb7ee7dfa6a?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(5) },
    { id: 11, nombre: 'Thor', raza: 'Husky Siberiano', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1568572933382-74d440642117?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, dueno: this.duenoService.getDuenoById(6) },
    { id: 12, nombre: 'Michi', raza: 'Gato Atigrado', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(6) },
    { id: 13, nombre: 'Buddy', raza: 'Pastor Alemán', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(7) },
    { id: 14, nombre: 'Salem', raza: 'Gato Naranja', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(7) },
    { id: 15, nombre: 'Duke', raza: 'Mestizo', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(8) },
    { id: 16, nombre: 'Whiskers', raza: 'Ragdoll', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(8) },
    { id: 17, nombre: 'Rex', raza: 'Chihuahua', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(9) },
    { id: 18, nombre: 'Nala', raza: 'Gato Mestizo', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1615789591457-74a63395c990?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(9) },
    { id: 19, nombre: 'Zeus', raza: 'Pug', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(10) },
    { id: 20, nombre: 'Simona', raza: 'Abisinio', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1571566882372-1598d88abd90?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, dueno: this.duenoService.getDuenoById(10) },
  ];

  getMascotas() {
    return this.mascotaArray;
  }

  getMascotasActivas() {
    return this.mascotaArray.filter((m) => m.activa);
  }

  getMascotasInactivas() {
    return this.mascotaArray.filter((m) => !m.activa);
  }

  getMascotaById(id: number) {
    return this.mascotaArray.find((m) => m.id === id);
  }

  // Recibe el objeto Dueno (relación como objeto) y compara por id
  getMascotasByDueno(dueno: Dueno) {
    return this.mascotaArray.filter((m) => m.dueno?.id === dueno.id);
  }

  addMascota(mascota: Mascota) {
    // Siguiente id = máximo actual + 1 (no se repite aunque se eliminen mascotas)
    mascota.id = Math.max(0, ...this.mascotaArray.map((m) => m.id)) + 1;
    this.mascotaArray.push(mascota);
  }

  updateMascota(id: number, mascota: Mascota) {
    const index = this.mascotaArray.findIndex((m) => m.id === id);
    if (index !== -1) {
      this.mascotaArray[index] = { ...mascota, id };
    }
  }

  desactivarMascota(mascota: Mascota) {
    mascota.activa = false;
  }

  activarMascota(mascota: Mascota) {
    mascota.activa = true;
  }

  deleteMascota(mascota: Mascota) {
    this.registroMedicoService.deleteRegistrosByMascota(mascota.id);
    this.mascotaArray = this.mascotaArray.filter((m) => m.id !== mascota.id);
  }
}
```

`src/app/pages/mascota-form/mascota-form.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';

@Component({
  selector: 'app-mascota-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './mascota-form.component.html',
  styleUrl: './mascota-form.component.scss',
})
export class MascotaFormComponent {
  //DI
  mascotaService = inject(MascotaService);
  duenoService = inject(DuenoService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  mascotaId: number | undefined = undefined;
  isEdit = false;
  noEncontrada = false;
  duenos: Dueno[] = [];

  // Se conserva el estado al editar para no reactivar una mascota inactiva
  private activaActual = true;

  mascotaForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    raza: new FormControl('', [Validators.maxLength(50)]),
    edad: new FormControl('', [Validators.maxLength(30)]),
    fotoUrl: new FormControl('', [Validators.pattern(/^https?:\/\/.+/)]),
    vacunas: new FormControl('', [Validators.maxLength(100)]),
    // El control guarda el objeto Dueno completo, no su id
    dueno: new FormControl<Dueno | null>(null, [Validators.required]),
  });

  // compareWith del <select>: compara dueños por id y no por referencia,
  // así al editar queda seleccionado el dueño que ya tenía la mascota
  compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);

  ngOnInit() {
    this.duenos = this.duenoService.getDuenos();

    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.mascotaId = Number(idParam);

      const mascota = this.mascotaService.getMascotaById(this.mascotaId);
      if (mascota) {
        this.activaActual = mascota.activa;
        this.mascotaForm.patchValue({
          nombre: mascota.nombre,
          raza: mascota.raza ?? '',
          edad: mascota.edad ?? '',
          fotoUrl: mascota.fotoUrl ?? '',
          vacunas: mascota.vacunas ?? '',
          dueno: mascota.dueno ?? null,
        });
      } else {
        this.noEncontrada = true;
      }
    }
  }

  // Muestra el error solo cuando el usuario ya tocó el campo
  campoInvalido(campo: string) {
    const control = this.mascotaForm.get(campo);
    return !!control && control.touched && control.invalid;
  }

  handleSubmit() {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      return;
    }

    const formValue = this.mascotaForm.value;

    const mascota: Mascota = {
      id: 0,
      nombre: formValue.nombre!.trim(),
      raza: formValue.raza?.trim() || undefined,
      edad: formValue.edad?.trim() || undefined,
      fotoUrl: formValue.fotoUrl?.trim() || undefined,
      vacunas: formValue.vacunas?.trim() || undefined,
      activa: this.isEdit ? this.activaActual : true,
      dueno: formValue.dueno ?? undefined,
    };

    if (this.isEdit) {
      this.mascotaService.updateMascota(this.mascotaId!, mascota);
    } else {
      this.mascotaService.addMascota(mascota);
    }

    this.router.navigate(['/mascotas']);
  }
}
```

`src/app/pages/mascota-form/mascota-form.component.html` (REEMPLAZAR):

```html
<div class="fondo py-5 px-3">
  <div class="row justify-content-center g-0">
    <div class="col-md-8 col-lg-5">
      <div class="card border-0 shadow rounded-4">
        <div class="card-body p-4 p-md-5">
          <h2 class="fw-bold text-dogtor text-center mb-4">
            @if (isEdit) { Editar Mascota } @else { Nueva Mascota }
          </h2>

          @if (noEncontrada) {
            <div class="alert alert-danger text-center">No se encontró la mascota.</div>
            <a [routerLink]="['/mascotas']" class="btn btn-dogtor w-100">Volver a mascotas</a>
          } @else {
            <form [formGroup]="mascotaForm" (ngSubmit)="handleSubmit()" novalidate>
              <!-- Nombre -->
              <div class="mb-3">
                <label for="nombre" class="form-label fw-bold">Nombre</label>
                <input
                  id="nombre"
                  type="text"
                  class="form-control"
                  formControlName="nombre"
                  [class.is-invalid]="campoInvalido('nombre')"
                />
                @if (campoInvalido('nombre')) {
                  <div class="invalid-feedback">
                    @if (mascotaForm.get('nombre')?.errors?.['required']) {
                      El nombre es obligatorio.
                    } @else if (mascotaForm.get('nombre')?.errors?.['minlength']) {
                      El nombre debe tener al menos 2 caracteres.
                    } @else if (mascotaForm.get('nombre')?.errors?.['maxlength']) {
                      El nombre no puede superar 50 caracteres.
                    } @else if (mascotaForm.get('nombre')?.errors?.['pattern']) {
                      El nombre solo puede contener letras.
                    }
                  </div>
                }
              </div>

              <!-- Raza -->
              <div class="mb-3">
                <label for="raza" class="form-label fw-bold">Raza</label>
                <input
                  id="raza"
                  type="text"
                  class="form-control"
                  formControlName="raza"
                  [class.is-invalid]="campoInvalido('raza')"
                />
                <div class="invalid-feedback">La raza no puede superar 50 caracteres.</div>
              </div>

              <!-- Edad -->
              <div class="mb-3">
                <label for="edad" class="form-label fw-bold">Edad</label>
                <input
                  id="edad"
                  type="text"
                  class="form-control"
                  placeholder="Ej: 2 años, 6 meses"
                  formControlName="edad"
                  [class.is-invalid]="campoInvalido('edad')"
                />
                <div class="invalid-feedback">La edad no puede superar 30 caracteres.</div>
              </div>

              <!-- Foto -->
              <div class="mb-3">
                <label for="fotoUrl" class="form-label fw-bold">Foto (URL)</label>
                <input
                  id="fotoUrl"
                  type="text"
                  class="form-control"
                  placeholder="https://..."
                  formControlName="fotoUrl"
                  [class.is-invalid]="campoInvalido('fotoUrl')"
                />
                <div class="invalid-feedback">La URL debe empezar por http:// o https://</div>
                @if (mascotaForm.get('fotoUrl')?.value && mascotaForm.get('fotoUrl')?.valid) {
                  <img [src]="mascotaForm.get('fotoUrl')?.value" alt="Vista previa" class="preview rounded-3 mt-2 border" />
                }
              </div>

              <!-- Vacunas -->
              <div class="mb-3">
                <label for="vacunas" class="form-label fw-bold">Vacunas</label>
                <input
                  id="vacunas"
                  type="text"
                  class="form-control"
                  formControlName="vacunas"
                  [class.is-invalid]="campoInvalido('vacunas')"
                />
                <div class="invalid-feedback">Las vacunas no pueden superar 100 caracteres.</div>
              </div>

              <!-- Dueño -->
              <div class="mb-4">
                <label for="dueno" class="form-label fw-bold">Dueño</label>
                <select
                  id="dueno"
                  class="form-select"
                  formControlName="dueno"
                  [compareWith]="compararDuenos"
                  [class.is-invalid]="campoInvalido('dueno')"
                >
                  <option [ngValue]="null" disabled>Selecciona un dueño...</option>
                  @for (dueno of duenos; track dueno.id) {
                    <option [ngValue]="dueno">{{ dueno.nombre }}</option>
                  }
                </select>
                <div class="invalid-feedback">Selecciona un dueño de la lista.</div>
              </div>

              <button type="submit" class="btn btn-dogtor-secondary w-100 py-2 rounded-3" [disabled]="!mascotaForm.valid">
                Guardar
              </button>
              <a [routerLink]="['/mascotas']" class="d-block text-center small text-muted mt-3 cancelar">Cancelar</a>
            </form>
          }
        </div>
      </div>
    </div>
  </div>
</div>
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se busca el texto `duenoId` en `src/app` THE SYSTEM SHALL encontrar cero coincidencias.
- [ ] WHEN se busca `getDuenoById` en los archivos `.ts` de `src/app` fuera de `src/app/service/` THE SYSTEM SHALL encontrar cero coincidencias.
- [ ] WHEN se lee `mascota-form.component.ts` THE SYSTEM SHALL declarar `dueno: new FormControl<Dueno | null>(null, [Validators.required]),`, precargar con `dueno: mascota.dueno ?? null,` y guardar con `dueno: formValue.dueno ?? undefined,`.
- [ ] WHEN se busca en `src/app/pages/mascota-form/` THE SYSTEM SHALL encontrar `compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);`, `[compareWith]="compararDuenos"` y `<option [ngValue]="dueno">`.
- [ ] WHEN se lee `src/app/service/mascota.service.ts` THE SYSTEM SHALL filtrar `getMascotasByDueno` con `m.dueno?.id === dueno.id`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -d src/app && test -z "$(grep -rn duenoId src/app)"  # expect: exit 0, cero ocurrencias
test -d src/app && test -z "$(grep -rn getDuenoById src/app --include=*.ts | grep -v '^src/app/service/')"  # expect: exit 0, solo se usa dentro de src/app/service/
test -f src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: new FormControl<Dueno | null>(null, [Validators.required]),' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: mascota.dueno ?? null,' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: formValue.dueno ?? undefined,' src/app/pages/mascota-form/mascota-form.component.ts  # expect: exit 0
test -d src/app/pages/mascota-form && grep -rqF 'compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);' src/app/pages/mascota-form && grep -rqF '[compareWith]="compararDuenos"' src/app/pages/mascota-form && grep -rqF '<option [ngValue]="dueno">' src/app/pages/mascota-form  # expect: exit 0
test -f src/app/service/mascota.service.ts && grep -qF 'm.dueno?.id === dueno.id' src/app/service/mascota.service.ts  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E1-T2: Formulario usa el objeto Dueno y se elimina duenoId" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null || git tag dogtor-s02
git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s01`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s01 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 3 — Componentes compartidos estado-badge y mascota-avatar

**Objetivo:** Después de este paso la foto de mascota y la etiqueta Activa/Inactiva son componentes compartidos con `input()`, usados por la tabla y el detalle.  
**Tarea:** `E2-T1` (epic `02-componentes-pequenos`) · **Depende de:** paso 2 · **Prioridad:** p1

**Do**

Crea dos componentes compartidos en `src/app/components/` (donde ya viven navbar y footer), siguiendo el patrón de `page-title` y `mascota-table`: standalone, `templateUrl` + `styleUrl`, `imports: []` explícito y entradas con `input()` / `input.required()`.

`mascota-avatar` es ahora el único dueño de la constante `fotoPorDefecto` (antes estaba duplicada en tabla y detalle) y fija el tamaño con `[style.width.px]`/`[style.height.px]`, por eso desaparecen las reglas `.foto` de las hojas de estilo de la tabla y del detalle. La tabla usa `tamano` 56 y el detalle 180. `estado-badge` reemplaza el `@if/@else` de badges del detalle.

Cada componente nuevo son 3 archivos (`.ts`, `.html`, `.scss`) que se crean juntos; en `tasks.json` se listan como un glob por carpeta.

##### Archivos

- `src/app/components/estado-badge/estado-badge.component.ts` — NUEVO
- `src/app/components/estado-badge/estado-badge.component.html` — NUEVO
- `src/app/components/estado-badge/estado-badge.component.scss` — NUEVO
- `src/app/components/mascota-avatar/mascota-avatar.component.ts` — NUEVO
- `src/app/components/mascota-avatar/mascota-avatar.component.html` — NUEVO
- `src/app/components/mascota-avatar/mascota-avatar.component.scss` — NUEVO
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html` — REEMPLAZAR
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.html` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.scss` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/components/estado-badge/estado-badge.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-estado-badge',
  imports: [],
  templateUrl: './estado-badge.component.html',
  styleUrl: './estado-badge.component.scss',
})
export class EstadoBadgeComponent {
  // Entrada: true = Activa, false = Inactiva
  activa = input.required<boolean>();
}
```

`src/app/components/estado-badge/estado-badge.component.html` (NUEVO):

```html
@if (activa()) {
  <span class="badge text-bg-success">Activa</span>
} @else {
  <span class="badge text-bg-secondary">Inactiva</span>
}
```

`src/app/components/estado-badge/estado-badge.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa las clases badge de Bootstrap */
```

`src/app/components/mascota-avatar/mascota-avatar.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-mascota-avatar',
  imports: [],
  templateUrl: './mascota-avatar.component.html',
  styleUrl: './mascota-avatar.component.scss',
})
export class MascotaAvatarComponent {
  // Entradas: la foto puede faltar, por eso hay una imagen por defecto
  fotoUrl = input<string | undefined>();
  nombre = input.required<string>();
  tamano = input<number>(56); // ancho y alto en px

  // Antes estaba repetida en la tabla y en el detalle; ahora vive solo aquí
  readonly fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';
}
```

`src/app/components/mascota-avatar/mascota-avatar.component.html` (NUEVO):

```html
<img
  [src]="fotoUrl() || fotoPorDefecto"
  [alt]="nombre()"
  [style.width.px]="tamano()"
  [style.height.px]="tamano()"
  class="foto rounded-circle border"
/>
```

`src/app/components/mascota-avatar/mascota-avatar.component.scss` (NUEVO):

```scss
.foto {
  object-fit: cover;
}
```

`src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts` (REEMPLAZAR):

```ts
import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';

@Component({
  selector: 'app-mascota-table',
  imports: [RouterLink, MascotaAvatarComponent],
  templateUrl: './mascota-table.component.html',
  styleUrl: './mascota-table.component.scss',
})
export class MascotaTableComponent {
  //DI
  router = inject(Router);

  // Entradas: datos que manda el componente padre
  mascotaArray = input<Mascota[]>([]);
  areMascotasActivas = input<boolean>(true);

  // Salidas: eventos que se le avisan al componente padre
  estadoCambiado = output<Mascota>();
  mascotaEliminada = output<Mascota>();

  verDetalleMascota(mascota: Mascota) {
    this.router.navigate(['/mascota', mascota.id]);
  }

  cambiarEstado(mascota: Mascota) {
    this.estadoCambiado.emit(mascota);
  }

  eliminarMascota(mascota: Mascota) {
    if (confirm(`¿Eliminar a ${mascota.nombre}? También se borrará su historial médico.`)) {
      this.mascotaEliminada.emit(mascota);
    }
  }
}
```

`src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html` (REEMPLAZAR):

```html
@if (mascotaArray().length === 0) {
  <div class="alert alert-light border text-center text-muted">No hay mascotas en esta lista.</div>
} @else {
  <div class="table-responsive bg-white rounded-3 shadow-sm border">
    <table class="table table-hover align-middle mb-0">
      <thead>
        <tr>
          <th scope="col">Foto</th>
          <th scope="col">ID</th>
          <th scope="col">Nombre</th>
          <th scope="col">Raza</th>
          <th scope="col">Edad</th>
          <th scope="col">Vacunas</th>
          <th scope="col">Dueño</th>
          <th scope="col">Acciones</th>
        </tr>
      </thead>
      <tbody>
        @for (mascota of mascotaArray(); track mascota.id) {
          <tr>
            <td>
              <app-mascota-avatar [fotoUrl]="mascota.fotoUrl" [nombre]="mascota.nombre" [tamano]="56"></app-mascota-avatar>
            </td>
            <td class="text-muted">{{ mascota.id }}</td>
            <td class="fw-bold text-dogtor">{{ mascota.nombre }}</td>
            <td>{{ mascota.raza }}</td>
            <td>{{ mascota.edad }}</td>
            <td>{{ mascota.vacunas }}</td>
            <td>{{ mascota.dueno?.nombre ?? '—' }}</td>
            <td>
              <div class="d-flex flex-wrap gap-2">
                <button type="button" class="btn btn-sm btn-dogtor" (click)="verDetalleMascota(mascota)">
                  <i class="bi bi-eye"></i> Ver
                </button>
                <a [routerLink]="['/mascota/update', mascota.id]" class="btn btn-sm btn-dogtor-accent">
                  <i class="bi bi-pencil"></i> Editar
                </a>
                @if (areMascotasActivas()) {
                  <button type="button" class="btn btn-sm btn-secondary" (click)="cambiarEstado(mascota)">
                    Desactivar
                  </button>
                } @else {
                  <button type="button" class="btn btn-sm btn-success" (click)="cambiarEstado(mascota)">
                    Activar
                  </button>
                }
                <button type="button" class="btn btn-sm btn-danger" (click)="eliminarMascota(mascota)">
                  <i class="bi bi-trash"></i> Eliminar
                </button>
              </div>
            </td>
          </tr>
        }
      </tbody>
    </table>
  </div>
}
```

`src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss` (REEMPLAZAR):

```scss
thead th {
  background-color: #f0fdfa;
  color: var(--dogtor-textdark);
  white-space: nowrap;
}
```

`src/app/pages/mascota-detail/mascota-detail.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, DatePipe, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  registros: RegistroMedico[] = [];

  // El dueño ya viene dentro de la mascota (objeto, no id): no hay que buscarlo por id
  get dueno() {
    return this.mascota?.dueno;
  }

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Cargar su historial
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
      this.registros = this.registroMedicoService.getRegistrosByMascota(this.mascotaId);
    }
  }

  getNombreVeterinario(veterinarioId?: number) {
    if (veterinarioId === undefined) {
      return 'Sin asignar';
    }
    return this.usuarioService.getUsuarioById(veterinarioId)?.nombre ?? 'Sin asignar';
  }

  getNombresDrogas(drogaIds: number[]) {
    return drogaIds
      .map((id) => this.drogaService.getDrogaById(id)?.nombre)
      .filter((nombre) => !!nombre)
      .join(', ');
  }
}
```

`src/app/pages/mascota-detail/mascota-detail.component.html` (REEMPLAZAR):

```html
<div class="container py-5">
  <a [routerLink]="['/mascotas']" class="volver small d-inline-block mb-4">
    <i class="bi bi-arrow-left"></i> Volver a mascotas
  </a>

  @if (mascota) {
    <div class="row justify-content-center">
      <div class="col-lg-10 col-xl-8">
        <!-- Datos de la mascota -->
        <div class="card border-0 shadow rounded-4 mb-4">
          <div class="card-body p-4">
            <div class="row align-items-center g-4">
              <div class="col-md-4 text-center">
                <app-mascota-avatar [fotoUrl]="mascota.fotoUrl" [nombre]="mascota.nombre" [tamano]="180"></app-mascota-avatar>
              </div>

              <div class="col-md-8">
                <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
                  <h1 class="h2 fw-bold text-dogtor mb-0">{{ mascota.nombre }}</h1>
                  <app-estado-badge [activa]="mascota.activa"></app-estado-badge>
                </div>

                <table class="table table-sm mb-3">
                  <tbody>
                    <tr><th>ID</th><td>{{ mascota.id }}</td></tr>
                    <tr><th>Raza</th><td>{{ mascota.raza || '—' }}</td></tr>
                    <tr><th>Edad</th><td>{{ mascota.edad || '—' }}</td></tr>
                    <tr><th>Vacunas</th><td>{{ mascota.vacunas || '—' }}</td></tr>
                    <tr>
                      <th>Dueño</th>
                      <td>
                        @if (dueno) {
                          {{ dueno.nombre }}
                          @if (dueno.telefono) {
                            <span class="text-muted">· {{ dueno.telefono }}</span>
                          }
                        } @else {
                          —
                        }
                      </td>
                    </tr>
                  </tbody>
                </table>

                <a [routerLink]="['/mascota/update', mascota.id]" class="btn btn-dogtor-accent">
                  <i class="bi bi-pencil"></i> Editar
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Historial médico -->
        <h2 class="h4 fw-bold mb-3">Historial médico</h2>
        @for (registro of registros; track registro.id) {
          <div class="card border-0 shadow-sm rounded-4 mb-3">
            <div class="card-body">
              <div class="d-flex flex-wrap justify-content-between gap-2 mb-2 small text-muted">
                <span><i class="bi bi-calendar3"></i> {{ registro.fecha | date: 'dd/MM/yyyy HH:mm' }}</span>
                <span><i class="bi bi-person-badge"></i> {{ getNombreVeterinario(registro.veterinarioId) }}</span>
              </div>
              <p class="mb-1"><strong>Diagnóstico:</strong> {{ registro.diagnostico }}</p>
              @if (registro.tratamiento) {
                <p class="mb-1"><strong>Tratamiento:</strong> {{ registro.tratamiento }}</p>
              }
              @if (registro.drogaIds.length > 0) {
                <p class="mb-0"><strong>Drogas:</strong> {{ getNombresDrogas(registro.drogaIds) }}</p>
              }
            </div>
          </div>
        } @empty {
          <div class="alert alert-light border text-muted">Esta mascota aún no tiene registros médicos.</div>
        }
      </div>
    </div>
  } @else {
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="alert alert-danger text-center">No se encontró la mascota con id {{ mascotaId }}.</div>
      </div>
    </div>
  }
</div>
```

`src/app/pages/mascota-detail/mascota-detail.component.scss` (REEMPLAZAR):

```scss
th {
  width: 35%;
  color: var(--dogtor-textdark);
}

.volver {
  color: var(--dogtor-textdark);
  text-decoration: none;

  &:hover {
    color: var(--dogtor-primary);
  }
}
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se listan `src/app/components/estado-badge/` y `src/app/components/mascota-avatar/` THE SYSTEM SHALL contener cada uno sus archivos `.component.ts`, `.component.html` y `.component.scss`, con `activa = input.required<boolean>();` y `nombre = input.required<string>();` respectivamente.
- [ ] WHEN se lee `mascota-table.component.html` THE SYSTEM SHALL usar `<app-mascota-avatar` con `[tamano]="56"`.
- [ ] WHEN se busca en `src/app/pages/mascota-detail/` THE SYSTEM SHALL encontrar `<app-mascota-avatar` con `[tamano]="180"` y `<app-estado-badge [activa]=`.
- [ ] WHEN se busca `fotoPorDefecto` en los `.ts` de `src/app` THE SYSTEM SHALL encontrarlo solo en `src/app/components/mascota-avatar/`.
- [ ] WHEN se leen `mascota-table.component.scss` y `mascota-detail.component.scss` THE SYSTEM SHALL no contener ninguna regla que empiece por `.foto`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
(for c in estado-badge mascota-avatar; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 6 archivos existen
grep -qF 'activa = input.required<boolean>();' src/app/components/estado-badge/estado-badge.component.ts && grep -qF 'nombre = input.required<string>();' src/app/components/mascota-avatar/mascota-avatar.component.ts  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '<app-mascota-avatar' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '[tamano]="56"' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app/pages/mascota-detail && grep -rqF '[tamano]="180"' src/app/pages/mascota-detail && grep -rqF '<app-estado-badge [activa]=' src/app/pages/mascota-detail  # expect: exit 0
test -d src/app && test -z "$(grep -rln fotoPorDefecto src/app --include=*.ts | grep -v '^src/app/components/mascota-avatar/')"  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss && test -f src/app/pages/mascota-detail/mascota-detail.component.scss && test -z "$(grep -n '^\.foto' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss src/app/pages/mascota-detail/mascota-detail.component.scss)"  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T1: Componentes compartidos estado-badge y mascota-avatar" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null || git tag dogtor-s03
git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s02`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s02 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 4 — Formulario de mascota dividido en componentes de campo

**Objetivo:** Después de este paso el formulario de mascota se arma con `app-campo-texto`, `app-dueno-select` y `app-foto-preview`, y su HTML tiene como máximo 70 líneas.  
**Tarea:** `E2-T2` (epic `02-componentes-pequenos`) · **Depende de:** paso 3 · **Prioridad:** p1

**Do**

Dos componentes compartidos nuevos en `src/app/components/`: `campo-error` (pinta el mensaje del primer error presente cuando el control está tocado e inválido) y `campo-texto` (label + `<input [formControl]>` + `campo-error`). Dos componentes locales de la página en `src/app/pages/mascota-form/components/`: `dueno-select` (se lleva `compararDuenos` y el `<select>`) y `foto-preview` (la vista previa de 96 px y su regla `.preview`).

`campo-error` **debe** renderizar `<div class="invalid-feedback d-block">`: el selector de Bootstrap `.is-invalid ~ .invalid-feedback` es de hermanos y no atraviesa el elemento host del componente, así que sin `d-block` el mensaje nunca se ve.

Las entradas de id se llaman `campoId` (no `id`): un `input` llamado `id` con atributo estático haría que Angular escriba el mismo id en el host y en el `<input>`, y el `<label>` apuntaría al host. El padre pasa los controles tipados (`mascotaForm.controls.nombre`, etc.) y un objeto `mensajes` con el texto de cada validador. `campoInvalido()` y el marcado duplicado de `invalid-feedback` salen del padre.

##### Archivos

- `src/app/components/campo-error/campo-error.component.ts` — NUEVO
- `src/app/components/campo-error/campo-error.component.html` — NUEVO
- `src/app/components/campo-error/campo-error.component.scss` — NUEVO
- `src/app/components/campo-texto/campo-texto.component.ts` — NUEVO
- `src/app/components/campo-texto/campo-texto.component.html` — NUEVO
- `src/app/components/campo-texto/campo-texto.component.scss` — NUEVO
- `src/app/pages/mascota-form/components/dueno-select/dueno-select.component.ts` — NUEVO
- `src/app/pages/mascota-form/components/dueno-select/dueno-select.component.html` — NUEVO
- `src/app/pages/mascota-form/components/dueno-select/dueno-select.component.scss` — NUEVO
- `src/app/pages/mascota-form/components/foto-preview/foto-preview.component.ts` — NUEVO
- `src/app/pages/mascota-form/components/foto-preview/foto-preview.component.html` — NUEVO
- `src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss` — NUEVO
- `src/app/pages/mascota-form/mascota-form.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-form/mascota-form.component.html` — REEMPLAZAR
- `src/app/pages/mascota-form/mascota-form.component.scss` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/components/campo-error/campo-error.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-campo-error',
  imports: [],
  templateUrl: './campo-error.component.html',
  styleUrl: './campo-error.component.scss',
})
export class CampoErrorComponent {
  // Entradas: el control a vigilar y el texto de cada error (clave = nombre del validador)
  control = input.required<AbstractControl>();
  mensajes = input<Record<string, string>>({});

  // Texto del primer error presente que tenga mensaje definido (devuelve un string, no un arreglo nuevo)
  mensaje() {
    const errores = this.control().errors;
    if (!errores) {
      return '';
    }
    const clave = Object.keys(errores).find((k) => this.mensajes()[k]);
    return clave ? this.mensajes()[clave] : '';
  }
}
```

`src/app/components/campo-error/campo-error.component.html` (NUEVO):

```html
<!-- d-block: el selector de Bootstrap ".is-invalid ~ .invalid-feedback" no atraviesa el host del componente -->
@if (control().touched && control().invalid) {
  <div class="invalid-feedback d-block">{{ mensaje() }}</div>
}
```

`src/app/components/campo-error/campo-error.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa .invalid-feedback de Bootstrap */
```

`src/app/components/campo-texto/campo-texto.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CampoErrorComponent } from '../campo-error/campo-error.component';

@Component({
  selector: 'app-campo-texto',
  imports: [ReactiveFormsModule, CampoErrorComponent],
  templateUrl: './campo-texto.component.html',
  styleUrl: './campo-texto.component.scss',
})
export class CampoTextoComponent {
  // Entradas. Se llama campoId (no "id") para que Angular no ponga el mismo id en el host y en el <input>
  campoId = input.required<string>();
  etiqueta = input.required<string>();
  control = input.required<FormControl<string | null>>();
  placeholder = input<string>('');
  mensajes = input<Record<string, string>>({});
}
```

`src/app/components/campo-texto/campo-texto.component.html` (NUEVO):

```html
<div class="mb-3">
  <label [attr.for]="campoId()" class="form-label fw-bold">{{ etiqueta() }}</label>
  <input
    [id]="campoId()"
    type="text"
    class="form-control"
    [placeholder]="placeholder()"
    [formControl]="control()"
    [class.is-invalid]="control().touched && control().invalid"
  />
  <app-campo-error [control]="control()" [mensajes]="mensajes()"></app-campo-error>
</div>
```

`src/app/components/campo-texto/campo-texto.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa .form-label y .form-control de Bootstrap */
```

`src/app/pages/mascota-form/components/dueno-select/dueno-select.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Dueno } from '../../../../models/dueno.model';
import { CampoErrorComponent } from '../../../../components/campo-error/campo-error.component';

@Component({
  selector: 'app-dueno-select',
  imports: [ReactiveFormsModule, CampoErrorComponent],
  templateUrl: './dueno-select.component.html',
  styleUrl: './dueno-select.component.scss',
})
export class DuenoSelectComponent {
  // Entradas: el control (guarda el objeto Dueno) y la lista de dueños para las opciones
  control = input.required<FormControl<Dueno | null>>();
  duenos = input.required<Dueno[]>();

  readonly mensajes = { required: 'Selecciona un dueño de la lista.' };

  // compareWith del <select>: compara dueños por id y no por referencia,
  // así al editar queda seleccionado el dueño que ya tenía la mascota
  compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);
}
```

`src/app/pages/mascota-form/components/dueno-select/dueno-select.component.html` (NUEVO):

```html
<div class="mb-4">
  <label for="dueno" class="form-label fw-bold">Dueño</label>
  <select
    id="dueno"
    class="form-select"
    [formControl]="control()"
    [compareWith]="compararDuenos"
    [class.is-invalid]="control().touched && control().invalid"
  >
    <option [ngValue]="null" disabled>Selecciona un dueño...</option>
    @for (dueno of duenos(); track dueno.id) {
      <option [ngValue]="dueno">{{ dueno.nombre }}</option>
    }
  </select>
  <app-campo-error [control]="control()" [mensajes]="mensajes"></app-campo-error>
</div>
```

`src/app/pages/mascota-form/components/dueno-select/dueno-select.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa .form-select de Bootstrap */
```

`src/app/pages/mascota-form/components/foto-preview/foto-preview.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-foto-preview',
  imports: [],
  templateUrl: './foto-preview.component.html',
  styleUrl: './foto-preview.component.scss',
})
export class FotoPreviewComponent {
  // Entrada: URL ya validada por el padre; si viene vacía no se muestra nada
  url = input<string | null | undefined>();
}
```

`src/app/pages/mascota-form/components/foto-preview/foto-preview.component.html` (NUEVO):

```html
@if (url()) {
  <img [src]="url()" alt="Vista previa" class="preview rounded-3 mb-3 border" />
}
```

`src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss` (NUEVO):

```scss
.preview {
  width: 96px;
  height: 96px;
  object-fit: cover;
}
```

`src/app/pages/mascota-form/mascota-form.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';
import { DuenoSelectComponent } from './components/dueno-select/dueno-select.component';
import { FotoPreviewComponent } from './components/foto-preview/foto-preview.component';

@Component({
  selector: 'app-mascota-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent, DuenoSelectComponent, FotoPreviewComponent],
  templateUrl: './mascota-form.component.html',
  styleUrl: './mascota-form.component.scss',
})
export class MascotaFormComponent {
  //DI
  mascotaService = inject(MascotaService);
  duenoService = inject(DuenoService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  mascotaId: number | undefined = undefined;
  isEdit = false;
  noEncontrada = false;
  duenos: Dueno[] = [];

  // Se conserva el estado al editar para no reactivar una mascota inactiva
  private activaActual = true;

  mascotaForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    raza: new FormControl('', [Validators.maxLength(50)]),
    edad: new FormControl('', [Validators.maxLength(30)]),
    fotoUrl: new FormControl('', [Validators.pattern(/^https?:\/\/.+/)]),
    vacunas: new FormControl('', [Validators.maxLength(100)]),
    // El control guarda el objeto Dueno completo, no su id
    dueno: new FormControl<Dueno | null>(null, [Validators.required]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    nombre: {
      required: 'El nombre es obligatorio.',
      minlength: 'El nombre debe tener al menos 2 caracteres.',
      maxlength: 'El nombre no puede superar 50 caracteres.',
      pattern: 'El nombre solo puede contener letras.',
    },
    raza: { maxlength: 'La raza no puede superar 50 caracteres.' },
    edad: { maxlength: 'La edad no puede superar 30 caracteres.' },
    fotoUrl: { pattern: 'La URL debe empezar por http:// o https://' },
    vacunas: { maxlength: 'Las vacunas no pueden superar 100 caracteres.' },
  };

  ngOnInit() {
    this.duenos = this.duenoService.getDuenos();

    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.mascotaId = Number(idParam);

      const mascota = this.mascotaService.getMascotaById(this.mascotaId);
      if (mascota) {
        this.activaActual = mascota.activa;
        this.mascotaForm.patchValue({
          nombre: mascota.nombre,
          raza: mascota.raza ?? '',
          edad: mascota.edad ?? '',
          fotoUrl: mascota.fotoUrl ?? '',
          vacunas: mascota.vacunas ?? '',
          dueno: mascota.dueno ?? null,
        });
      } else {
        this.noEncontrada = true;
      }
    }
  }

  handleSubmit() {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      return;
    }

    const formValue = this.mascotaForm.value;

    const mascota: Mascota = {
      id: 0,
      nombre: formValue.nombre!.trim(),
      raza: formValue.raza?.trim() || undefined,
      edad: formValue.edad?.trim() || undefined,
      fotoUrl: formValue.fotoUrl?.trim() || undefined,
      vacunas: formValue.vacunas?.trim() || undefined,
      activa: this.isEdit ? this.activaActual : true,
      dueno: formValue.dueno ?? undefined,
    };

    if (this.isEdit) {
      this.mascotaService.updateMascota(this.mascotaId!, mascota);
    } else {
      this.mascotaService.addMascota(mascota);
    }

    this.router.navigate(['/mascotas']);
  }
}
```

`src/app/pages/mascota-form/mascota-form.component.html` (REEMPLAZAR):

```html
<div class="fondo py-5 px-3">
  <div class="row justify-content-center g-0">
    <div class="col-md-8 col-lg-5">
      <div class="card border-0 shadow rounded-4">
        <div class="card-body p-4 p-md-5">
          <h2 class="fw-bold text-dogtor text-center mb-4">
            @if (isEdit) { Editar Mascota } @else { Nueva Mascota }
          </h2>

          @if (noEncontrada) {
            <div class="alert alert-danger text-center">No se encontró la mascota.</div>
            <a [routerLink]="['/mascotas']" class="btn btn-dogtor w-100">Volver a mascotas</a>
          } @else {
            <form [formGroup]="mascotaForm" (ngSubmit)="handleSubmit()" novalidate>
              <app-campo-texto
                campoId="nombre"
                etiqueta="Nombre"
                [control]="mascotaForm.controls.nombre"
                [mensajes]="mensajes.nombre"
              ></app-campo-texto>
              <app-campo-texto
                campoId="raza"
                etiqueta="Raza"
                [control]="mascotaForm.controls.raza"
                [mensajes]="mensajes.raza"
              ></app-campo-texto>
              <app-campo-texto
                campoId="edad"
                etiqueta="Edad"
                placeholder="Ej: 2 años, 6 meses"
                [control]="mascotaForm.controls.edad"
                [mensajes]="mensajes.edad"
              ></app-campo-texto>
              <app-campo-texto
                campoId="fotoUrl"
                etiqueta="Foto (URL)"
                placeholder="https://..."
                [control]="mascotaForm.controls.fotoUrl"
                [mensajes]="mensajes.fotoUrl"
              ></app-campo-texto>
              <app-foto-preview
                [url]="mascotaForm.controls.fotoUrl.valid ? mascotaForm.controls.fotoUrl.value : null"
              ></app-foto-preview>
              <app-campo-texto
                campoId="vacunas"
                etiqueta="Vacunas"
                [control]="mascotaForm.controls.vacunas"
                [mensajes]="mensajes.vacunas"
              ></app-campo-texto>
              <app-dueno-select [control]="mascotaForm.controls.dueno" [duenos]="duenos"></app-dueno-select>

              <button type="submit" class="btn btn-dogtor-secondary w-100 py-2 rounded-3" [disabled]="!mascotaForm.valid">
                Guardar
              </button>
              <a [routerLink]="['/mascotas']" class="d-block text-center small text-muted mt-3 cancelar">Cancelar</a>
            </form>
          }
        </div>
      </div>
    </div>
  </div>
</div>
```

`src/app/pages/mascota-form/mascota-form.component.scss` (REEMPLAZAR):

```scss
.fondo {
  background-color: #f0fdfa;
  min-height: 100%;
}

.cancelar {
  text-decoration: none;

  &:hover {
    color: var(--dogtor-primary) !important;
  }
}
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se cuentan las líneas de `src/app/pages/mascota-form/mascota-form.component.html` THE SYSTEM SHALL reportar 70 o menos.
- [ ] WHEN se lee `src/app/components/campo-error/campo-error.component.html` THE SYSTEM SHALL renderizar `<div class="invalid-feedback d-block">`.
- [ ] WHEN se lee `mascota-form.component.html` THE SYSTEM SHALL contener `campoId="nombre"`, `campoId="raza"`, `campoId="edad"`, `campoId="fotoUrl"`, `campoId="vacunas"`, `<app-foto-preview` y `<app-dueno-select`.
- [ ] WHEN se buscan `campoInvalido` e `invalid-feedback` en `mascota-form.component.ts` y `mascota-form.component.html` THE SYSTEM SHALL encontrar cero coincidencias.
- [ ] WHEN se buscan reglas `.preview` THE SYSTEM SHALL encontrarla en `foto-preview.component.scss` y no en `mascota-form.component.scss`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/mascota-form/mascota-form.component.html && test "$(wc -l < src/app/pages/mascota-form/mascota-form.component.html)" -le 70  # expect: exit 0 (≤ 70 líneas)
(for c in campo-error campo-texto; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done; for c in dueno-select foto-preview; do for e in ts html scss; do test -f src/app/pages/mascota-form/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 12 archivos existen
grep -qF '<div class="invalid-feedback d-block">' src/app/components/campo-error/campo-error.component.html  # expect: exit 0
f=src/app/pages/mascota-form/mascota-form.component.html; test -f $f && grep -qF 'campoId="nombre"' $f && grep -qF 'campoId="raza"' $f && grep -qF 'campoId="edad"' $f && grep -qF 'campoId="fotoUrl"' $f && grep -qF 'campoId="vacunas"' $f && grep -qF '<app-foto-preview' $f && grep -qF '<app-dueno-select' $f  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.ts && test -f src/app/pages/mascota-form/mascota-form.component.html && test -z "$(grep -nE 'campoInvalido|invalid-feedback' src/app/pages/mascota-form/mascota-form.component.ts src/app/pages/mascota-form/mascota-form.component.html)"  # expect: exit 0
grep -qF '.preview {' src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss && test -f src/app/pages/mascota-form/mascota-form.component.scss && test -z "$(grep -n 'preview' src/app/pages/mascota-form/mascota-form.component.scss)"  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T2: Formulario de mascota dividido en componentes de campo" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null || git tag dogtor-s04
git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s03`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s03 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 5 — Detalle de mascota dividido en tarjetas presentacionales

**Objetivo:** Después de este paso el detalle se arma con `app-mascota-info-card`, `app-dueno-card` y `app-registro-medico-item`, con nombres resueltos una sola vez, y su HTML tiene como máximo 45 líneas.  
**Tarea:** `E2-T3` (epic `02-componentes-pequenos`) · **Depende de:** paso 4 · **Prioridad:** p1

**Do**

Un componente compartido nuevo, `dueno-card` (`src/app/components/dueno-card/`), que pinta nombre, teléfono y dirección con iconos `bi` o "Sin dueño asignado". Dos componentes locales en `src/app/pages/mascota-detail/components/`: `mascota-info-card` (compone `mascota-avatar`, `estado-badge`, la tabla de datos, `dueno-card` y el botón Editar; se lleva la regla `th`) y `registro-medico-item` (solo pinta, con `DatePipe`).

Los componentes hijos son presentacionales: el **padre** resuelve una sola vez, en `ngOnInit`, el nombre del veterinario y los nombres de las drogas y guarda `registros: RegistroVista[]`. No se llaman métodos desde el template que devuelvan arreglos nuevos: cada detección de cambios vería un valor distinto y en modo desarrollo Angular lanza NG0100 (ExpressionChangedAfterItHasBeenChecked). `RegistroMedico` no cambia (sigue con `veterinarioId` y `drogaIds`, ver No-objetivos).

El getter `dueno` del paso 1 y los métodos `getNombreVeterinario`/`getNombresDrogas` públicos desaparecen del detalle.

##### Archivos

- `src/app/components/dueno-card/dueno-card.component.ts` — NUEVO
- `src/app/components/dueno-card/dueno-card.component.html` — NUEVO
- `src/app/components/dueno-card/dueno-card.component.scss` — NUEVO
- `src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.ts` — NUEVO
- `src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.html` — NUEVO
- `src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.scss` — NUEVO
- `src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts` — NUEVO
- `src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.html` — NUEVO
- `src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.scss` — NUEVO
- `src/app/pages/mascota-detail/mascota-detail.component.ts` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.html` — REEMPLAZAR
- `src/app/pages/mascota-detail/mascota-detail.component.scss` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/components/dueno-card/dueno-card.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { Dueno } from '../../models/dueno.model';

@Component({
  selector: 'app-dueno-card',
  imports: [],
  templateUrl: './dueno-card.component.html',
  styleUrl: './dueno-card.component.scss',
})
export class DuenoCardComponent {
  // Entrada: el dueño ya viene dentro de la mascota (objeto, no id); puede no tener
  dueno = input<Dueno | undefined>();
}
```

`src/app/components/dueno-card/dueno-card.component.html` (NUEVO):

```html
@if (dueno(); as duenoActual) {
  <div class="border rounded-3 p-3 bg-light">
    <p class="fw-bold mb-2"><i class="bi bi-person"></i> {{ duenoActual.nombre }}</p>
    <p class="small mb-1"><i class="bi bi-telephone"></i> {{ duenoActual.telefono || '—' }}</p>
    <p class="small mb-0"><i class="bi bi-geo-alt"></i> {{ duenoActual.direccion || '—' }}</p>
  </div>
} @else {
  <p class="text-muted mb-0">Sin dueño asignado</p>
}
```

`src/app/components/dueno-card/dueno-card.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa utilidades de Bootstrap */
```

`src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../../../components/estado-badge/estado-badge.component';
import { DuenoCardComponent } from '../../../../components/dueno-card/dueno-card.component';

@Component({
  selector: 'app-mascota-info-card',
  imports: [RouterLink, MascotaAvatarComponent, EstadoBadgeComponent, DuenoCardComponent],
  templateUrl: './mascota-info-card.component.html',
  styleUrl: './mascota-info-card.component.scss',
})
export class MascotaInfoCardComponent {
  // Entrada: la mascota a mostrar (el padre ya verificó que existe)
  mascota = input.required<Mascota>();
}
```

`src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.html` (NUEVO):

```html
<div class="card border-0 shadow rounded-4 mb-4">
  <div class="card-body p-4">
    <div class="row align-items-center g-4">
      <div class="col-md-4 text-center">
        <app-mascota-avatar [fotoUrl]="mascota().fotoUrl" [nombre]="mascota().nombre" [tamano]="180"></app-mascota-avatar>
      </div>

      <div class="col-md-8">
        <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
          <h1 class="h2 fw-bold text-dogtor mb-0">{{ mascota().nombre }}</h1>
          <app-estado-badge [activa]="mascota().activa"></app-estado-badge>
        </div>

        <table class="table table-sm mb-3">
          <tbody>
            <tr><th>ID</th><td>{{ mascota().id }}</td></tr>
            <tr><th>Raza</th><td>{{ mascota().raza || '—' }}</td></tr>
            <tr><th>Edad</th><td>{{ mascota().edad || '—' }}</td></tr>
            <tr><th>Vacunas</th><td>{{ mascota().vacunas || '—' }}</td></tr>
          </tbody>
        </table>

        <h2 class="h6 fw-bold mb-2">Dueño</h2>
        <app-dueno-card [dueno]="mascota().dueno"></app-dueno-card>

        <a [routerLink]="['/mascota/update', mascota().id]" class="btn btn-dogtor-accent mt-3">
          <i class="bi bi-pencil"></i> Editar
        </a>
      </div>
    </div>
  </div>
</div>
```

`src/app/pages/mascota-detail/components/mascota-info-card/mascota-info-card.component.scss` (NUEVO):

```scss
th {
  width: 35%;
  color: var(--dogtor-textdark);
}
```

`src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RegistroMedico } from '../../../../models/registro-medico.model';

@Component({
  selector: 'app-registro-medico-item',
  imports: [DatePipe],
  templateUrl: './registro-medico-item.component.html',
  styleUrl: './registro-medico-item.component.scss',
})
export class RegistroMedicoItemComponent {
  // Entradas: el padre ya resolvió los nombres; este componente solo pinta
  registro = input.required<RegistroMedico>();
  veterinario = input.required<string>();
  drogas = input<string[]>([]);
}
```

`src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.html` (NUEVO):

```html
<div class="card border-0 shadow-sm rounded-4 mb-3">
  <div class="card-body">
    <div class="d-flex flex-wrap justify-content-between gap-2 mb-2 small text-muted">
      <span><i class="bi bi-calendar3"></i> {{ registro().fecha | date: 'dd/MM/yyyy HH:mm' }}</span>
      <span><i class="bi bi-person-badge"></i> {{ veterinario() }}</span>
    </div>
    <p class="mb-1"><strong>Diagnóstico:</strong> {{ registro().diagnostico }}</p>
    @if (registro().tratamiento) {
      <p class="mb-1"><strong>Tratamiento:</strong> {{ registro().tratamiento }}</p>
    }
    @if (drogas().length > 0) {
      <p class="mb-0"><strong>Drogas:</strong> {{ drogas().join(', ') }}</p>
    }
  </div>
</div>
```

`src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa .card de Bootstrap */
```

`src/app/pages/mascota-detail/mascota-detail.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';
import { MascotaInfoCardComponent } from './components/mascota-info-card/mascota-info-card.component';
import { RegistroMedicoItemComponent } from './components/registro-medico-item/registro-medico-item.component';

// Registro médico con los nombres ya resueltos, listo para pintarse
interface RegistroVista {
  registro: RegistroMedico;
  veterinario: string;
  drogas: string[];
}

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, MascotaInfoCardComponent, RegistroMedicoItemComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;

  // Se arma UNA vez en ngOnInit. Si el template llamara a un método que devuelve un arreglo
  // nuevo, cada detección de cambios vería un valor distinto (error NG0100 en desarrollo)
  registros: RegistroVista[] = [];

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Preparar su historial
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
      this.registros = this.registroMedicoService.getRegistrosByMascota(this.mascotaId).map((registro) => ({
        registro,
        veterinario: this.getNombreVeterinario(registro.veterinarioId),
        drogas: registro.drogaIds
          .map((id) => this.drogaService.getDrogaById(id)?.nombre)
          .filter((nombre): nombre is string => !!nombre),
      }));
    }
  }

  private getNombreVeterinario(veterinarioId?: number) {
    if (veterinarioId === undefined) {
      return 'Sin asignar';
    }
    return this.usuarioService.getUsuarioById(veterinarioId)?.nombre ?? 'Sin asignar';
  }
}
```

`src/app/pages/mascota-detail/mascota-detail.component.html` (REEMPLAZAR):

```html
<div class="container py-5">
  <a [routerLink]="['/mascotas']" class="volver small d-inline-block mb-4">
    <i class="bi bi-arrow-left"></i> Volver a mascotas
  </a>

  @if (mascota) {
    <div class="row justify-content-center">
      <div class="col-lg-10 col-xl-8">
        <app-mascota-info-card [mascota]="mascota"></app-mascota-info-card>

        <!-- Historial médico -->
        <h2 class="h4 fw-bold mb-3">Historial médico</h2>
        @for (item of registros; track item.registro.id) {
          <app-registro-medico-item
            [registro]="item.registro"
            [veterinario]="item.veterinario"
            [drogas]="item.drogas"
          ></app-registro-medico-item>
        } @empty {
          <div class="alert alert-light border text-muted">Esta mascota aún no tiene registros médicos.</div>
        }
      </div>
    </div>
  } @else {
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="alert alert-danger text-center">No se encontró la mascota con id {{ mascotaId }}.</div>
      </div>
    </div>
  }
</div>
```

`src/app/pages/mascota-detail/mascota-detail.component.scss` (REEMPLAZAR):

```scss
.volver {
  color: var(--dogtor-textdark);
  text-decoration: none;

  &:hover {
    color: var(--dogtor-primary);
  }
}
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se cuentan las líneas de `src/app/pages/mascota-detail/mascota-detail.component.html` THE SYSTEM SHALL reportar 45 o menos.
- [ ] WHEN se lee `mascota-detail.component.html` THE SYSTEM SHALL contener `<app-mascota-info-card` y `<app-registro-medico-item` y cero llamadas a `getNombre`.
- [ ] WHEN se lee `mascota-detail.component.ts` THE SYSTEM SHALL declarar `registros: RegistroVista[] = [];` y no inyectar `DuenoService`.
- [ ] WHEN se lee `src/app/components/dueno-card/dueno-card.component.html` THE SYSTEM SHALL contener el texto `Sin dueño asignado` y el componente SHALL declarar `dueno = input<Dueno | undefined>();`.
- [ ] WHEN se lee `registro-medico-item.component.ts` THE SYSTEM SHALL declarar `registro = input.required<RegistroMedico>();`, `veterinario = input.required<string>();` y `drogas = input<string[]>([]);` sin inyectar ningún servicio.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && test "$(wc -l < src/app/pages/mascota-detail/mascota-detail.component.html)" -le 45  # expect: exit 0 (≤ 45 líneas)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-mascota-info-card' src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-registro-medico-item' src/app/pages/mascota-detail/mascota-detail.component.html && test -z "$(grep -n 'getNombre' src/app/pages/mascota-detail/mascota-detail.component.html)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.ts && grep -qF 'registros: RegistroVista[] = [];' src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -n 'DuenoService' src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0
f=src/app/components/dueno-card/dueno-card.component; test -f $f.html && grep -qF 'Sin dueño asignado' $f.html && grep -qF 'dueno = input<Dueno | undefined>();' $f.ts  # expect: exit 0
f=src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts; test -f $f && grep -qF 'registro = input.required<RegistroMedico>();' $f && grep -qF 'veterinario = input.required<string>();' $f && grep -qF 'drogas = input<string[]>([]);' $f && test -z "$(grep -n 'inject(' $f)"  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T3: Detalle de mascota dividido en tarjetas presentacionales" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null || git tag dogtor-s05
git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s04`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s04 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 6 — LandingService, hero-carousel y fuera el botón de WhatsApp

**Objetivo:** Después de este paso la landing toma sus datos de `LandingService`, el carrusel es `app-hero-carousel` y el botón flotante de WhatsApp ya no existe.  
**Tarea:** `E3-T1` (epic `03-landing-completa`) · **Depende de:** paso 5 · **Prioridad:** p1

**Do**

Crea `src/app/models/landing.model.ts` con las interfaces tipadas de la landing y `src/app/service/landing.service.ts` (`providedIn: 'root'`, arreglos quemados como los demás servicios) con **todos** los datos de la landing: las 2 diapositivas actuales (mismas URLs y textos), 6 servicios, 3 planes en pesos colombianos, 3 miembros del equipo, 3 testimonios y los datos de contacto. Los pasos 7–9 solo consumen esos getters.

El carrusel actual se mueve **sin cambiar comportamiento** a `src/app/pages/landing/components/hero-carousel/` (mismo marcado, misma lógica `anterior`/`siguiente`, mismo SCSS copiado tal cual); solo cambia `slides` → `slides()` porque ahora es `input.required<Slide[]>()`.

Se elimina el botón flotante de WhatsApp: el bloque `<a ... class="whatsapp-btn ...">` y su comentario en `app.component.html`, y la regla `.whatsapp-btn` de `app.component.scss`.

##### Archivos

- `src/app/models/landing.model.ts` — NUEVO
- `src/app/service/landing.service.ts` — NUEVO
- `src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts` — NUEVO
- `src/app/pages/landing/components/hero-carousel/hero-carousel.component.html` — NUEVO
- `src/app/pages/landing/components/hero-carousel/hero-carousel.component.scss` — NUEVO
- `src/app/pages/landing/landing.component.ts` — REEMPLAZAR
- `src/app/pages/landing/landing.component.html` — REEMPLAZAR
- `src/app/pages/landing/landing.component.scss` — REEMPLAZAR
- `src/app/app.component.html` — REEMPLAZAR
- `src/app/app.component.scss` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/models/landing.model.ts` (NUEVO):

```ts
// Modelos de la landing. No son entidades de Spring Boot: son el contenido de la página de inicio

export interface Slide {
  img: string;
  title: string;
  desc: string;
  link?: string;
  btn?: string;
}

export interface Servicio {
  icono: string; // clase de bootstrap-icons, ej. "bi-heart-pulse"
  titulo: string;
  descripcion: string;
}

export interface Plan {
  nombre: string;
  precio: string; // ya formateado en pesos colombianos, ej. "$49.900"
  periodo: string;
  beneficios: string[];
  destacado: boolean;
}

export interface MiembroEquipo {
  nombre: string;
  cargo: string;
  descripcion: string;
}

export interface Testimonio {
  autor: string;
  mascota: string;
  texto: string;
  estrellas: number; // de 1 a 5
}

export interface DatosContacto {
  direccion: string;
  telefono: string;
  correo: string;
  horario: string;
}
```

`src/app/service/landing.service.ts` (NUEVO):

```ts
import { Injectable } from '@angular/core';
import { DatosContacto, MiembroEquipo, Plan, Servicio, Slide, Testimonio } from '../models/landing.model';

@Injectable({
  providedIn: 'root',
})
export class LandingService {
  // Datos de la landing, igual que los demás servicios: arreglos quemados porque no hay backend
  private slides: Slide[] = [
    {
      img: 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=1200',
      title: 'Cuidamos a tus Pacientes',
      desc: 'Consulta el historial médico de tu mascota en un solo lugar.',
      link: '/mascotas',
      btn: 'Ver Mascotas',
    },
    {
      img: 'https://images.unsplash.com/photo-1537151608804-ea6f117f73d2?w=1200',
      title: 'Nuestro Equipo',
      desc: 'Conoce a los especialistas de DogTor.',
    },
  ];

  private servicios: Servicio[] = [
    { icono: 'bi-heart-pulse', titulo: 'Consulta general', descripcion: 'Revisión completa del estado de salud de tu mascota con nuestros veterinarios.' },
    { icono: 'bi-shield-check', titulo: 'Vacunación', descripcion: 'Esquemas de vacunación al día para perros y gatos de todas las edades.' },
    { icono: 'bi-scissors', titulo: 'Cirugía', descripcion: 'Procedimientos quirúrgicos con anestesia monitoreada y control posoperatorio.' },
    { icono: 'bi-capsule', titulo: 'Farmacia', descripcion: 'Medicamentos veterinarios formulados por nuestros especialistas.' },
    { icono: 'bi-house-heart', titulo: 'Hospitalización', descripcion: 'Cuidado las 24 horas para los pacientes que necesitan observación.' },
    { icono: 'bi-droplet', titulo: 'Peluquería y baño', descripcion: 'Baño, corte y limpieza para que tu mascota luzca y se sienta bien.' },
  ];

  private planes: Plan[] = [
    {
      nombre: 'Básico',
      precio: '$49.900',
      periodo: '/mes',
      beneficios: ['1 consulta general al mes', 'Carné de vacunación digital', '10% de descuento en farmacia'],
      destacado: false,
    },
    {
      nombre: 'Plus',
      precio: '$89.900',
      periodo: '/mes',
      beneficios: [
        '2 consultas generales al mes',
        'Vacunación anual incluida',
        'Desparasitación cada 3 meses',
        '15% de descuento en farmacia',
      ],
      destacado: true,
    },
    {
      nombre: 'Premium',
      precio: '$149.900',
      periodo: '/mes',
      beneficios: [
        'Consultas generales ilimitadas',
        'Vacunación y desparasitación incluidas',
        'Baño y peluquería una vez al mes',
        '20% de descuento en cirugías',
        'Atención prioritaria',
      ],
      destacado: false,
    },
  ];

  // Los dos veterinarios son los mismos usuarios VETERINARIO de UsuarioService
  private equipo: MiembroEquipo[] = [
    { nombre: 'Dr. Andres Felipe', cargo: 'Médico veterinario', descripcion: 'Medicina general y control de pacientes caninos y felinos.' },
    { nombre: 'Dra. Laura Jimenez', cargo: 'Médica veterinaria', descripcion: 'Dermatología y tratamiento de alergias en mascotas.' },
    { nombre: 'Paola Méndez', cargo: 'Recepción y atención al cliente', descripcion: 'Agenda tus citas y resuelve tus dudas sobre los planes.' },
  ];

  private testimonios: Testimonio[] = [
    { autor: 'Juan Pérez', mascota: 'Max', texto: 'Atendieron a Max muy rápido y nos explicaron todo el tratamiento.', estrellas: 5 },
    { autor: 'María López', mascota: 'Rocky', texto: 'El plan Plus nos ha ahorrado mucho en vacunas y consultas.', estrellas: 5 },
    { autor: 'Sofía Martínez', mascota: 'Thor', texto: 'Muy buena atención; a veces hay que esperar un poco en recepción.', estrellas: 4 },
  ];

  private contacto: DatosContacto = {
    direccion: 'Calle Falsa 123, Bogotá',
    telefono: '+57 123 456 7890',
    correo: 'contacto@dogtor.com',
    horario: 'Lunes a sábado, 8:00 a. m. a 6:00 p. m.',
  };

  getSlides() {
    return this.slides;
  }

  getServicios() {
    return this.servicios;
  }

  getPlanes() {
    return this.planes;
  }

  getEquipo() {
    return this.equipo;
  }

  getTestimonios() {
    return this.testimonios;
  }

  getContacto() {
    return this.contacto;
  }
}
```

`src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Slide } from '../../../../models/landing.model';

@Component({
  selector: 'app-hero-carousel',
  imports: [RouterLink],
  templateUrl: './hero-carousel.component.html',
  styleUrl: './hero-carousel.component.scss',
})
export class HeroCarouselComponent {
  // Entrada: las diapositivas vienen de LandingService a través del padre
  slides = input.required<Slide[]>();

  activeSlide = 0;

  anterior() {
    this.activeSlide = this.activeSlide === 0 ? this.slides().length - 1 : this.activeSlide - 1;
  }

  siguiente() {
    this.activeSlide = this.activeSlide === this.slides().length - 1 ? 0 : this.activeSlide + 1;
  }
}
```

`src/app/pages/landing/components/hero-carousel/hero-carousel.component.html` (NUEVO):

```html
<section class="hero position-relative w-100 overflow-hidden bg-dark">
  @for (slide of slides(); track $index) {
    <div class="slide position-absolute top-0 start-0 w-100 h-100" [class.visible]="activeSlide === $index">
      <img [src]="slide.img" [alt]="slide.title" class="w-100 h-100" />
      <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column align-items-center justify-content-center text-center px-5">
        <h2 class="display-4 fw-bold text-white mb-3">{{ slide.title }}</h2>
        <p class="fs-5 text-white mb-4">{{ slide.desc }}</p>
        @if (slide.link) {
          <a [routerLink]="slide.link" class="btn btn-dogtor btn-lg px-5">{{ slide.btn }}</a>
        }
      </div>
    </div>
  }

  <button type="button" class="control start-0 ms-3" (click)="anterior()" aria-label="Anterior">
    <i class="bi bi-chevron-left"></i>
  </button>
  <button type="button" class="control end-0 me-3" (click)="siguiente()" aria-label="Siguiente">
    <i class="bi bi-chevron-right"></i>
  </button>

  <div class="indicadores position-absolute bottom-0 start-50 translate-middle-x mb-3 d-flex gap-2">
    @for (slide of slides(); track $index) {
      <button
        type="button"
        [class.activo]="activeSlide === $index"
        (click)="activeSlide = $index"
        [attr.aria-label]="'Ir a ' + slide.title"
      ></button>
    }
  </div>
</section>
```

`src/app/pages/landing/components/hero-carousel/hero-carousel.component.scss` (NUEVO):

```scss
.hero {
  height: 500px;
}

.slide {
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.5s, visibility 0.5s;

  &.visible {
    opacity: 1;
    visibility: visible;
  }

  img {
    object-fit: cover;
    opacity: 0.6;
  }
}

.control {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  border: 0;
  border-radius: 50%;
  width: 2.75rem;
  height: 2.75rem;
  color: #fff;
  background-color: rgba(255, 255, 255, 0.3);
  transition: background-color 0.2s;

  &:hover {
    background-color: rgba(255, 255, 255, 0.5);
  }
}

.indicadores button {
  width: 0.75rem;
  height: 0.75rem;
  border: 0;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);

  &.activo {
    background-color: #fff;
  }
}
```

`src/app/pages/landing/landing.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { LandingService } from '../../service/landing.service';
import { Slide } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [HeroCarouselComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
}
```

`src/app/pages/landing/landing.component.html` (REEMPLAZAR):

```html
<app-hero-carousel [slides]="slides"></app-hero-carousel>
```

`src/app/pages/landing/landing.component.scss` (REEMPLAZAR):

```scss
/* Sin estilos propios: el carrusel tiene los suyos y las secciones usan utilidades de Bootstrap */
```

`src/app/app.component.html` (REEMPLAZAR):

```html
<app-navbar></app-navbar>

<main>
  <router-outlet></router-outlet>
</main>

<app-footer></app-footer>
```

`src/app/app.component.scss` (REEMPLAZAR):

```scss
:host {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

main {
  flex-grow: 1;
}
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se lee `src/app/service/landing.service.ts` THE SYSTEM SHALL declarar `providedIn: 'root'`, los getters `getSlides`, `getServicios`, `getPlanes`, `getEquipo`, `getTestimonios` y `getContacto`, y las dos URLs de diapositiva `photo-1552053831-71594a27632d?w=1200` y `photo-1537151608804-ea6f117f73d2?w=1200`.
- [ ] WHEN se busca `unsplash` en `src/app/pages/landing/landing.component.ts` THE SYSTEM SHALL encontrar cero coincidencias.
- [ ] WHEN se lee `hero-carousel.component.ts` THE SYSTEM SHALL declarar `slides = input.required<Slide[]>();` y `landing.component.html` SHALL contener `<app-hero-carousel [slides]="slides">`.
- [ ] WHEN se busca `whatsapp` sin distinguir mayúsculas en `src/app` THE SYSTEM SHALL encontrar cero coincidencias.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/service/landing.service.ts; test -f $f && grep -qF "providedIn: 'root'" $f && grep -qF 'getSlides()' $f && grep -qF 'getServicios()' $f && grep -qF 'getPlanes()' $f && grep -qF 'getEquipo()' $f && grep -qF 'getTestimonios()' $f && grep -qF 'getContacto()' $f  # expect: exit 0
f=src/app/service/landing.service.ts; grep -qF 'photo-1552053831-71594a27632d?w=1200' $f && grep -qF 'photo-1537151608804-ea6f117f73d2?w=1200' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.ts && test -z "$(grep -n unsplash src/app/pages/landing/landing.component.ts)"  # expect: exit 0
grep -qF 'slides = input.required<Slide[]>();' src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts && grep -qF '<app-hero-carousel [slides]="slides">' src/app/pages/landing/landing.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rni whatsapp src/app)"  # expect: exit 0, cero coincidencias
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T1: LandingService, hero-carousel y fuera el botón de WhatsApp" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null || git tag dogtor-s06
git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s05`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s05 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 7 — Secciones de servicios y planes con service-card y plan-card

**Objetivo:** Después de este paso la landing muestra 6 servicios y 3 planes, y elegir un plan lo guarda en `planSeleccionado` y hace scroll a `#contacto`.  
**Tarea:** `E3-T2` (epic `03-landing-completa`) · **Depende de:** paso 6 · **Prioridad:** p1

**Do**

Tres componentes locales en `src/app/pages/landing/components/`: `section-header` (entradas `titulo`, `subtitulo` y `ancla` opcional que se pinta como `id` del encabezado; se llama `ancla` y no `id` por la misma razón que `campoId` en el paso 4), `service-card` (entrada `servicio`) y `plan-card` (entrada `plan` y **salida** `planElegido = output<Plan>()`, emitida por su botón).

La landing guarda el plan recibido en `planSeleccionado` y hace `document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' })`. El encabezado con `ancla="contacto"` llega en el paso 9; hasta entonces `getElementById` devuelve `null` y el `?.` lo convierte en un no-op, así que el build y la página siguen funcionando.

##### Archivos

- `src/app/pages/landing/components/section-header/section-header.component.ts` — NUEVO
- `src/app/pages/landing/components/section-header/section-header.component.html` — NUEVO
- `src/app/pages/landing/components/section-header/section-header.component.scss` — NUEVO
- `src/app/pages/landing/components/service-card/service-card.component.ts` — NUEVO
- `src/app/pages/landing/components/service-card/service-card.component.html` — NUEVO
- `src/app/pages/landing/components/service-card/service-card.component.scss` — NUEVO
- `src/app/pages/landing/components/plan-card/plan-card.component.ts` — NUEVO
- `src/app/pages/landing/components/plan-card/plan-card.component.html` — NUEVO
- `src/app/pages/landing/components/plan-card/plan-card.component.scss` — NUEVO
- `src/app/pages/landing/landing.component.ts` — REEMPLAZAR
- `src/app/pages/landing/landing.component.html` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/pages/landing/components/section-header/section-header.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-section-header',
  imports: [],
  templateUrl: './section-header.component.html',
  styleUrl: './section-header.component.scss',
})
export class SectionHeaderComponent {
  titulo = input.required<string>();
  subtitulo = input<string>('');
  // Ancla opcional para navegar o hacer scroll a la sección. Se llama "ancla" (no "id")
  // para que Angular no repita el id en el host del componente
  ancla = input<string | undefined>();
}
```

`src/app/pages/landing/components/section-header/section-header.component.html` (NUEVO):

```html
<div class="encabezado text-center mb-5" [attr.id]="ancla()">
  <h2 class="fw-bold">{{ titulo() }}</h2>
  @if (subtitulo()) {
    <p class="text-muted mb-0">{{ subtitulo() }}</p>
  }
</div>
```

`src/app/pages/landing/components/section-header/section-header.component.scss` (NUEVO):

```scss
// El navbar es sticky: este margen evita que tape el título al hacer scroll hasta el ancla
.encabezado {
  scroll-margin-top: 6rem;
}

h2 {
  color: var(--dogtor-textdark);
}
```

`src/app/pages/landing/components/service-card/service-card.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { Servicio } from '../../../../models/landing.model';

@Component({
  selector: 'app-service-card',
  imports: [],
  templateUrl: './service-card.component.html',
  styleUrl: './service-card.component.scss',
})
export class ServiceCardComponent {
  // Entrada: el servicio a mostrar
  servicio = input.required<Servicio>();
}
```

`src/app/pages/landing/components/service-card/service-card.component.html` (NUEVO):

```html
<div class="card h-100 border-0 shadow-sm rounded-4 text-center p-4">
  <i class="bi {{ servicio().icono }} icono text-dogtor mb-3"></i>
  <h3 class="h5 fw-bold">{{ servicio().titulo }}</h3>
  <p class="text-muted mb-0">{{ servicio().descripcion }}</p>
</div>
```

`src/app/pages/landing/components/service-card/service-card.component.scss` (NUEVO):

```scss
.icono {
  font-size: 2.5rem;
}
```

`src/app/pages/landing/components/plan-card/plan-card.component.ts` (NUEVO):

```ts
import { Component, input, output } from '@angular/core';
import { Plan } from '../../../../models/landing.model';

@Component({
  selector: 'app-plan-card',
  imports: [],
  templateUrl: './plan-card.component.html',
  styleUrl: './plan-card.component.scss',
})
export class PlanCardComponent {
  // Entrada: el plan a mostrar
  plan = input.required<Plan>();

  // Salida: avisa al padre qué plan eligió el usuario
  planElegido = output<Plan>();

  elegir() {
    this.planElegido.emit(this.plan());
  }
}
```

`src/app/pages/landing/components/plan-card/plan-card.component.html` (NUEVO):

```html
<div class="card h-100 rounded-4 shadow-sm" [class.destacado]="plan().destacado" [class.border-0]="!plan().destacado">
  <div class="card-body p-4 d-flex flex-column">
    @if (plan().destacado) {
      <span class="badge bg-dogtor-dark align-self-start mb-2">Más elegido</span>
    }
    <h3 class="h4 fw-bold">{{ plan().nombre }}</h3>
    <p class="mb-3">
      <span class="display-6 fw-bold text-dogtor">{{ plan().precio }}</span>
      <span class="text-muted">{{ plan().periodo }}</span>
    </p>
    <ul class="list-unstyled d-grid gap-2 mb-4">
      @for (beneficio of plan().beneficios; track beneficio) {
        <li><i class="bi bi-check-circle-fill text-dogtor"></i> {{ beneficio }}</li>
      }
    </ul>
    <button
      type="button"
      class="btn mt-auto"
      [class.btn-dogtor]="plan().destacado"
      [class.btn-dogtor-secondary]="!plan().destacado"
      (click)="elegir()"
    >
      Elegir plan {{ plan().nombre }}
    </button>
  </div>
</div>
```

`src/app/pages/landing/components/plan-card/plan-card.component.scss` (NUEVO):

```scss
.destacado {
  border: 2px solid var(--dogtor-primary);
}
```

`src/app/pages/landing/landing.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { SectionHeaderComponent } from './components/section-header/section-header.component';
import { ServiceCardComponent } from './components/service-card/service-card.component';
import { PlanCardComponent } from './components/plan-card/plan-card.component';
import { LandingService } from '../../service/landing.service';
import { Plan, Servicio, Slide } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [HeroCarouselComponent, SectionHeaderComponent, ServiceCardComponent, PlanCardComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
  servicios: Servicio[] = this.landingService.getServicios();
  planes: Plan[] = this.landingService.getPlanes();

  // Plan elegido en una plan-card; el formulario de contacto lo usa para precargar el mensaje
  planSeleccionado: Plan | undefined;

  elegirPlan(plan: Plan) {
    this.planSeleccionado = plan;
    // Lleva al usuario hasta la sección de contacto (el encabezado con ancla "contacto")
    document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });
  }
}
```

`src/app/pages/landing/landing.component.html` (REEMPLAZAR):

```html
<app-hero-carousel [slides]="slides"></app-hero-carousel>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="servicios" titulo="Nuestros servicios" subtitulo="Todo lo que tu mascota necesita, en un solo lugar."></app-section-header>
    <div class="row g-4">
      @for (servicio of servicios; track servicio.titulo) {
        <div class="col-md-6 col-lg-4"><app-service-card [servicio]="servicio"></app-service-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5 bg-white">
  <div class="container">
    <app-section-header ancla="planes" titulo="Planes de salud" subtitulo="Elige el plan que mejor se adapta a tu mascota."></app-section-header>
    <div class="row g-4 justify-content-center">
      @for (plan of planes; track plan.nombre) {
        <div class="col-md-6 col-lg-4"><app-plan-card [plan]="plan" (planElegido)="elegirPlan($event)"></app-plan-card></div>
      }
    </div>
  </div>
</section>
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se lee `plan-card.component.ts` THE SYSTEM SHALL declarar `plan = input.required<Plan>();` y `planElegido = output<Plan>();`.
- [ ] WHEN se lee `landing.component.html` THE SYSTEM SHALL contener `<app-section-header`, `<app-service-card` y `(planElegido)="elegirPlan($event)"`.
- [ ] WHEN se lee `landing.component.ts` THE SYSTEM SHALL contener `document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });` y `planSeleccionado: Plan | undefined;`.
- [ ] WHEN se lee `src/app/service/landing.service.ts` THE SYSTEM SHALL contener los iconos `bi-heart-pulse`, `bi-shield-check`, `bi-scissors`, `bi-capsule`, `bi-house-heart` y `bi-droplet`, y los precios `$49.900`, `$89.900` y `$149.900`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/pages/landing/components/plan-card/plan-card.component.ts; test -f $f && grep -qF 'plan = input.required<Plan>();' $f && grep -qF 'planElegido = output<Plan>();' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-section-header' $f && grep -qF '<app-service-card' $f && grep -qF '(planElegido)="elegirPlan($event)"' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.ts; test -f $f && grep -qF "document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });" $f && grep -qF 'planSeleccionado: Plan | undefined;' $f  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && (for i in bi-heart-pulse bi-shield-check bi-scissors bi-capsule bi-house-heart bi-droplet; do grep -qF "'$i'" $f || exit 1; done)  # expect: exit 0, los 6 iconos
f=src/app/service/landing.service.ts; grep -qF "precio: '\$49.900'" $f && grep -qF "precio: '\$89.900'" $f && grep -qF "precio: '\$149.900'" $f  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T2: Secciones de servicios y planes con service-card y plan-card" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null || git tag dogtor-s07
git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s06`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s06 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 8 — Secciones de equipo, testimonios y banner a la acción

**Objetivo:** Después de este paso la landing muestra el equipo, los testimonios con estrellas y un banner que lleva a `/mascotas`.  
**Tarea:** `E3-T3` (epic `03-landing-completa`) · **Depende de:** paso 7 · **Prioridad:** p2

**Do**

Tres componentes locales en `src/app/pages/landing/components/`: `team-card` (entrada `miembro`; avatar con el icono `bi-person-circle`, sin fotos externas que se puedan romper), `testimonio-card` (entrada `testimonio`; pinta 5 iconos usando un arreglo fijo `posiciones`, `bi-star-fill` para las llenas y `bi-star` para las vacías) y `cta-banner` (entradas `titulo`, `texto`, `textoBoton`, `ruta`; botón con `[routerLink]="ruta()"`).

El equipo son los dos veterinarios que ya existen en `UsuarioService` ("Dr. Andres Felipe" y "Dra. Laura Jimenez") más una persona ficticia de recepción; los datos ya están en `LandingService` desde el paso 6.

##### Archivos

- `src/app/pages/landing/components/team-card/team-card.component.ts` — NUEVO
- `src/app/pages/landing/components/team-card/team-card.component.html` — NUEVO
- `src/app/pages/landing/components/team-card/team-card.component.scss` — NUEVO
- `src/app/pages/landing/components/testimonio-card/testimonio-card.component.ts` — NUEVO
- `src/app/pages/landing/components/testimonio-card/testimonio-card.component.html` — NUEVO
- `src/app/pages/landing/components/testimonio-card/testimonio-card.component.scss` — NUEVO
- `src/app/pages/landing/components/cta-banner/cta-banner.component.ts` — NUEVO
- `src/app/pages/landing/components/cta-banner/cta-banner.component.html` — NUEVO
- `src/app/pages/landing/components/cta-banner/cta-banner.component.scss` — NUEVO
- `src/app/pages/landing/landing.component.ts` — REEMPLAZAR
- `src/app/pages/landing/landing.component.html` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/pages/landing/components/team-card/team-card.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { MiembroEquipo } from '../../../../models/landing.model';

@Component({
  selector: 'app-team-card',
  imports: [],
  templateUrl: './team-card.component.html',
  styleUrl: './team-card.component.scss',
})
export class TeamCardComponent {
  // Entrada: el miembro del equipo a mostrar
  miembro = input.required<MiembroEquipo>();
}
```

`src/app/pages/landing/components/team-card/team-card.component.html` (NUEVO):

```html
<!-- Ícono en vez de foto: no dependemos de URLs externas que se pueden romper -->
<div class="card h-100 border-0 shadow-sm rounded-4 text-center p-4">
  <i class="bi bi-person-circle avatar text-dogtor mb-3"></i>
  <h3 class="h5 fw-bold mb-1">{{ miembro().nombre }}</h3>
  <p class="text-dogtor fw-semibold small mb-2">{{ miembro().cargo }}</p>
  <p class="text-muted mb-0">{{ miembro().descripcion }}</p>
</div>
```

`src/app/pages/landing/components/team-card/team-card.component.scss` (NUEVO):

```scss
.avatar {
  font-size: 4rem;
  line-height: 1;
}
```

`src/app/pages/landing/components/testimonio-card/testimonio-card.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { Testimonio } from '../../../../models/landing.model';

@Component({
  selector: 'app-testimonio-card',
  imports: [],
  templateUrl: './testimonio-card.component.html',
  styleUrl: './testimonio-card.component.scss',
})
export class TestimonioCardComponent {
  // Entrada: el testimonio a mostrar
  testimonio = input.required<Testimonio>();

  // Posiciones de las 5 estrellas; es un arreglo fijo para no crear uno nuevo en cada detección de cambios
  readonly posiciones = [1, 2, 3, 4, 5];
}
```

`src/app/pages/landing/components/testimonio-card/testimonio-card.component.html` (NUEVO):

```html
<div class="card h-100 border-0 shadow-sm rounded-4 p-4">
  <div class="estrellas mb-3" [attr.aria-label]="testimonio().estrellas + ' de 5 estrellas'">
    @for (posicion of posiciones; track posicion) {
      <i class="bi" [class.bi-star-fill]="posicion <= testimonio().estrellas" [class.bi-star]="posicion > testimonio().estrellas"></i>
    }
  </div>
  <p class="fst-italic mb-3">"{{ testimonio().texto }}"</p>
  <p class="fw-bold mb-0">
    {{ testimonio().autor }}
    <span class="text-muted fw-normal">· Mascota: {{ testimonio().mascota }}</span>
  </p>
</div>
```

`src/app/pages/landing/components/testimonio-card/testimonio-card.component.scss` (NUEVO):

```scss
.estrellas {
  color: var(--dogtor-accent);
}
```

`src/app/pages/landing/components/cta-banner/cta-banner.component.ts` (NUEVO):

```ts
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cta-banner',
  imports: [RouterLink],
  templateUrl: './cta-banner.component.html',
  styleUrl: './cta-banner.component.scss',
})
export class CtaBannerComponent {
  // Entradas: textos del banner y la ruta interna a la que lleva el botón
  titulo = input.required<string>();
  texto = input<string>('');
  textoBoton = input.required<string>();
  ruta = input.required<string>();
}
```

`src/app/pages/landing/components/cta-banner/cta-banner.component.html` (NUEVO):

```html
<section class="bg-dogtor-dark text-white py-5">
  <div class="container text-center">
    <h2 class="fw-bold mb-2">{{ titulo() }}</h2>
    @if (texto()) {
      <p class="mb-4 opacity-75">{{ texto() }}</p>
    }
    <a [routerLink]="ruta()" class="btn btn-dogtor-secondary btn-lg px-5">{{ textoBoton() }}</a>
  </div>
</section>
```

`src/app/pages/landing/components/cta-banner/cta-banner.component.scss` (NUEVO):

```scss
/* Sin estilos propios: usa .bg-dogtor-dark de styles.scss y utilidades de Bootstrap */
```

`src/app/pages/landing/landing.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { SectionHeaderComponent } from './components/section-header/section-header.component';
import { ServiceCardComponent } from './components/service-card/service-card.component';
import { PlanCardComponent } from './components/plan-card/plan-card.component';
import { TeamCardComponent } from './components/team-card/team-card.component';
import { TestimonioCardComponent } from './components/testimonio-card/testimonio-card.component';
import { CtaBannerComponent } from './components/cta-banner/cta-banner.component';
import { LandingService } from '../../service/landing.service';
import { MiembroEquipo, Plan, Servicio, Slide, Testimonio } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [
    HeroCarouselComponent,
    SectionHeaderComponent,
    ServiceCardComponent,
    PlanCardComponent,
    TeamCardComponent,
    TestimonioCardComponent,
    CtaBannerComponent,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
  servicios: Servicio[] = this.landingService.getServicios();
  planes: Plan[] = this.landingService.getPlanes();
  equipo: MiembroEquipo[] = this.landingService.getEquipo();
  testimonios: Testimonio[] = this.landingService.getTestimonios();

  // Plan elegido en una plan-card; el formulario de contacto lo usa para precargar el mensaje
  planSeleccionado: Plan | undefined;

  elegirPlan(plan: Plan) {
    this.planSeleccionado = plan;
    // Lleva al usuario hasta la sección de contacto (el encabezado con ancla "contacto")
    document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });
  }
}
```

`src/app/pages/landing/landing.component.html` (REEMPLAZAR):

```html
<app-hero-carousel [slides]="slides"></app-hero-carousel>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="servicios" titulo="Nuestros servicios" subtitulo="Todo lo que tu mascota necesita, en un solo lugar."></app-section-header>
    <div class="row g-4">
      @for (servicio of servicios; track servicio.titulo) {
        <div class="col-md-6 col-lg-4"><app-service-card [servicio]="servicio"></app-service-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5 bg-white">
  <div class="container">
    <app-section-header ancla="planes" titulo="Planes de salud" subtitulo="Elige el plan que mejor se adapta a tu mascota."></app-section-header>
    <div class="row g-4 justify-content-center">
      @for (plan of planes; track plan.nombre) {
        <div class="col-md-6 col-lg-4"><app-plan-card [plan]="plan" (planElegido)="elegirPlan($event)"></app-plan-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="equipo" titulo="Nuestro equipo" subtitulo="Especialistas que cuidan a tus mascotas como si fueran suyas."></app-section-header>
    <div class="row g-4 justify-content-center">
      @for (miembro of equipo; track miembro.nombre) {
        <div class="col-md-6 col-lg-4"><app-team-card [miembro]="miembro"></app-team-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5 bg-white">
  <div class="container">
    <app-section-header ancla="testimonios" titulo="Lo que dicen nuestros clientes"></app-section-header>
    <div class="row g-4">
      @for (testimonio of testimonios; track testimonio.autor) {
        <div class="col-md-6 col-lg-4"><app-testimonio-card [testimonio]="testimonio"></app-testimonio-card></div>
      }
    </div>
  </div>
</section>

<app-cta-banner
  titulo="¿Ya eres parte de la familia DogTor?"
  texto="Consulta la información y el historial médico de tus mascotas."
  textoBoton="Ver mascotas"
  ruta="/mascotas"
></app-cta-banner>
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se lee `landing.component.html` THE SYSTEM SHALL contener `<app-team-card`, `<app-testimonio-card`, `<app-cta-banner` y `ruta="/mascotas"`.
- [ ] WHEN se leen `team-card.component.html` y `testimonio-card.component.html` THE SYSTEM SHALL usar `bi-person-circle` y `bi-star-fill` respectivamente.
- [ ] WHEN se lee `cta-banner.component.ts` THE SYSTEM SHALL declarar `ruta = input.required<string>();` y `cta-banner.component.html` SHALL contener `[routerLink]="ruta()"`.
- [ ] WHEN se lee `src/app/service/landing.service.ts` THE SYSTEM SHALL contener `Dr. Andres Felipe` y `Dra. Laura Jimenez`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-team-card' $f && grep -qF '<app-testimonio-card' $f && grep -qF '<app-cta-banner' $f && grep -qF 'ruta="/mascotas"' $f  # expect: exit 0
grep -qF 'bi-person-circle' src/app/pages/landing/components/team-card/team-card.component.html && grep -qF 'bi-star-fill' src/app/pages/landing/components/testimonio-card/testimonio-card.component.html  # expect: exit 0
grep -qF 'ruta = input.required<string>();' src/app/pages/landing/components/cta-banner/cta-banner.component.ts && grep -qF '[routerLink]="ruta()"' src/app/pages/landing/components/cta-banner/cta-banner.component.html  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF 'Dr. Andres Felipe' $f && grep -qF 'Dra. Laura Jimenez' $f  # expect: exit 0
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T3: Secciones de equipo, testimonios y banner a la acción" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null || git tag dogtor-s08
git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s07`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s07 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 9 — Formulario de contacto con plan precargado (sin envío real)

**Objetivo:** Después de este paso la landing termina en una sección de contacto con los datos de la clínica y un formulario validado que precarga el plan elegido; su HTML tiene como máximo 60 líneas.  
**Tarea:** `E3-T4` (epic `03-landing-completa`) · **Depende de:** paso 8 · **Prioridad:** p1

**Do**

Un componente local `contacto-form` en `src/app/pages/landing/components/` con entradas `datos = input.required<DatosContacto>()` y `plan = input<Plan | undefined>()`, y **salida** `enviado`. Formulario reactivo: `nombre` (requerido, mínimo 2), `correo` (requerido, email) y `mensaje` (requerido, mínimo 10). Reutiliza `app-campo-texto` para nombre y correo, y `app-campo-error` para el `<textarea>`.

Se eligió `effect()` (no `ngOnChanges`): en el constructor, cuando `plan()` trae un plan, se hace `setValue("Me interesa el plan <nombre>.")` sobre `mensaje`. Al enviar un formulario válido emite `enviado`, hace `reset()` y muestra la alerta "¡Gracias! Te contactaremos pronto.". **No se envía nada a ningún lado**: no hay backend ni `HttpClient`. La landing limpia `planSeleccionado` al recibir `enviado`.

La sección de contacto es la última de la landing y su encabezado lleva `ancla="contacto"`, que es el destino del scroll del paso 7.

##### Archivos

- `src/app/pages/landing/components/contacto-form/contacto-form.component.ts` — NUEVO
- `src/app/pages/landing/components/contacto-form/contacto-form.component.html` — NUEVO
- `src/app/pages/landing/components/contacto-form/contacto-form.component.scss` — NUEVO
- `src/app/pages/landing/landing.component.ts` — REEMPLAZAR
- `src/app/pages/landing/landing.component.html` — REEMPLAZAR

Escribe cada archivo **exactamente** con este contenido (cuerpo completo; `REEMPLAZAR` = sobrescribir el archivo entero). Indentación de 2 espacios y salto de línea final.

`src/app/pages/landing/components/contacto-form/contacto-form.component.ts` (NUEVO):

```ts
import { Component, effect, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatosContacto, Plan } from '../../../../models/landing.model';
import { CampoTextoComponent } from '../../../../components/campo-texto/campo-texto.component';
import { CampoErrorComponent } from '../../../../components/campo-error/campo-error.component';

@Component({
  selector: 'app-contacto-form',
  imports: [ReactiveFormsModule, CampoTextoComponent, CampoErrorComponent],
  templateUrl: './contacto-form.component.html',
  styleUrl: './contacto-form.component.scss',
})
export class ContactoFormComponent {
  // Entradas: datos de la clínica y el plan elegido en la sección de planes (si hay uno)
  datos = input.required<DatosContacto>();
  plan = input<Plan | undefined>();

  // Salida: avisa al padre que se envió el formulario. No se manda a ningún servidor (no hay backend)
  enviado = output<{ nombre: string; correo: string; mensaje: string }>();

  enviadoConExito = false;

  contactoForm = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(2)]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    mensaje: new FormControl('', [Validators.required, Validators.minLength(10)]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    nombre: { required: 'El nombre es obligatorio.', minlength: 'El nombre debe tener al menos 2 caracteres.' },
    correo: { required: 'El correo es obligatorio.', email: 'Escribe un correo válido.' },
    mensaje: { required: 'El mensaje es obligatorio.', minlength: 'El mensaje debe tener al menos 10 caracteres.' },
  };

  constructor() {
    // Cada vez que llega un plan nuevo desde la landing, se precarga el mensaje
    effect(() => {
      const plan = this.plan();
      if (plan) {
        this.contactoForm.controls.mensaje.setValue(`Me interesa el plan ${plan.nombre}.`);
        this.enviadoConExito = false;
      }
    });
  }

  handleSubmit() {
    if (this.contactoForm.invalid) {
      this.contactoForm.markAllAsTouched();
      return;
    }

    const valor = this.contactoForm.getRawValue();
    this.enviado.emit({
      nombre: (valor.nombre ?? '').trim(),
      correo: (valor.correo ?? '').trim(),
      mensaje: (valor.mensaje ?? '').trim(),
    });
    this.contactoForm.reset();
    this.enviadoConExito = true;
  }
}
```

`src/app/pages/landing/components/contacto-form/contacto-form.component.html` (NUEVO):

```html
<div class="row g-4">
  <div class="col-lg-5">
    <div class="card h-100 border-0 shadow-sm rounded-4 p-4">
      <h3 class="h5 fw-bold mb-3">Visítanos o escríbenos</h3>
      <p class="mb-2"><i class="bi bi-geo-alt text-dogtor"></i> {{ datos().direccion }}</p>
      <p class="mb-2"><i class="bi bi-telephone text-dogtor"></i> {{ datos().telefono }}</p>
      <p class="mb-2"><i class="bi bi-envelope text-dogtor"></i> {{ datos().correo }}</p>
      <p class="mb-0"><i class="bi bi-clock text-dogtor"></i> {{ datos().horario }}</p>
    </div>
  </div>

  <div class="col-lg-7">
    <div class="card border-0 shadow-sm rounded-4 p-4">
      @if (enviadoConExito) {
        <div class="alert alert-success" role="status">¡Gracias! Te contactaremos pronto.</div>
      }
      <form [formGroup]="contactoForm" (ngSubmit)="handleSubmit()" novalidate>
        <app-campo-texto
          campoId="contacto-nombre"
          etiqueta="Nombre"
          [control]="contactoForm.controls.nombre"
          [mensajes]="mensajes.nombre"
        ></app-campo-texto>
        <app-campo-texto
          campoId="contacto-correo"
          etiqueta="Correo"
          placeholder="tucorreo@ejemplo.com"
          [control]="contactoForm.controls.correo"
          [mensajes]="mensajes.correo"
        ></app-campo-texto>
        <div class="mb-3">
          <label for="contacto-mensaje" class="form-label fw-bold">Mensaje</label>
          <textarea
            id="contacto-mensaje"
            rows="4"
            class="form-control"
            formControlName="mensaje"
            [class.is-invalid]="contactoForm.controls.mensaje.touched && contactoForm.controls.mensaje.invalid"
          ></textarea>
          <app-campo-error [control]="contactoForm.controls.mensaje" [mensajes]="mensajes.mensaje"></app-campo-error>
        </div>
        <button type="submit" class="btn btn-dogtor w-100">Enviar mensaje</button>
      </form>
    </div>
  </div>
</div>
```

`src/app/pages/landing/components/contacto-form/contacto-form.component.scss` (NUEVO):

```scss
/* Sin estilos propios: reutiliza app-campo-texto, app-campo-error y utilidades de Bootstrap */
```

`src/app/pages/landing/landing.component.ts` (REEMPLAZAR):

```ts
import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { SectionHeaderComponent } from './components/section-header/section-header.component';
import { ServiceCardComponent } from './components/service-card/service-card.component';
import { PlanCardComponent } from './components/plan-card/plan-card.component';
import { TeamCardComponent } from './components/team-card/team-card.component';
import { TestimonioCardComponent } from './components/testimonio-card/testimonio-card.component';
import { CtaBannerComponent } from './components/cta-banner/cta-banner.component';
import { ContactoFormComponent } from './components/contacto-form/contacto-form.component';
import { LandingService } from '../../service/landing.service';
import { DatosContacto, MiembroEquipo, Plan, Servicio, Slide, Testimonio } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [
    HeroCarouselComponent,
    SectionHeaderComponent,
    ServiceCardComponent,
    PlanCardComponent,
    TeamCardComponent,
    TestimonioCardComponent,
    CtaBannerComponent,
    ContactoFormComponent,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
  servicios: Servicio[] = this.landingService.getServicios();
  planes: Plan[] = this.landingService.getPlanes();
  equipo: MiembroEquipo[] = this.landingService.getEquipo();
  testimonios: Testimonio[] = this.landingService.getTestimonios();
  contacto: DatosContacto = this.landingService.getContacto();

  // Plan elegido en una plan-card; el formulario de contacto lo usa para precargar el mensaje
  planSeleccionado: Plan | undefined;

  elegirPlan(plan: Plan) {
    this.planSeleccionado = plan;
    // Lleva al usuario hasta la sección de contacto (el encabezado con ancla "contacto")
    document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });
  }

  // El formulario no se envía a ningún servidor: solo se limpia el plan elegido
  contactoEnviado() {
    this.planSeleccionado = undefined;
  }
}
```

`src/app/pages/landing/landing.component.html` (REEMPLAZAR):

```html
<app-hero-carousel [slides]="slides"></app-hero-carousel>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="servicios" titulo="Nuestros servicios" subtitulo="Todo lo que tu mascota necesita, en un solo lugar."></app-section-header>
    <div class="row g-4">
      @for (servicio of servicios; track servicio.titulo) {
        <div class="col-md-6 col-lg-4"><app-service-card [servicio]="servicio"></app-service-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5 bg-white">
  <div class="container">
    <app-section-header ancla="planes" titulo="Planes de salud" subtitulo="Elige el plan que mejor se adapta a tu mascota."></app-section-header>
    <div class="row g-4 justify-content-center">
      @for (plan of planes; track plan.nombre) {
        <div class="col-md-6 col-lg-4"><app-plan-card [plan]="plan" (planElegido)="elegirPlan($event)"></app-plan-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="equipo" titulo="Nuestro equipo" subtitulo="Especialistas que cuidan a tus mascotas como si fueran suyas."></app-section-header>
    <div class="row g-4 justify-content-center">
      @for (miembro of equipo; track miembro.nombre) {
        <div class="col-md-6 col-lg-4"><app-team-card [miembro]="miembro"></app-team-card></div>
      }
    </div>
  </div>
</section>

<section class="py-5 bg-white">
  <div class="container">
    <app-section-header ancla="testimonios" titulo="Lo que dicen nuestros clientes"></app-section-header>
    <div class="row g-4">
      @for (testimonio of testimonios; track testimonio.autor) {
        <div class="col-md-6 col-lg-4"><app-testimonio-card [testimonio]="testimonio"></app-testimonio-card></div>
      }
    </div>
  </div>
</section>

<app-cta-banner
  titulo="¿Ya eres parte de la familia DogTor?"
  texto="Consulta la información y el historial médico de tus mascotas."
  textoBoton="Ver mascotas"
  ruta="/mascotas"
></app-cta-banner>

<section class="py-5">
  <div class="container">
    <app-section-header ancla="contacto" titulo="Contáctanos" subtitulo="Agenda una cita o pregúntanos por nuestros planes."></app-section-header>
    <app-contacto-form [datos]="contacto" [plan]="planSeleccionado" (enviado)="contactoEnviado()"></app-contacto-form>
  </div>
</section>
```

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0.
- [ ] WHEN se cuentan las líneas de `src/app/pages/landing/landing.component.html` THE SYSTEM SHALL reportar 60 o menos.
- [ ] WHEN se lee `landing.component.html` THE SYSTEM SHALL contener `<app-hero-carousel`, `<app-service-card`, `<app-plan-card`, `<app-team-card`, `<app-testimonio-card`, `<app-cta-banner`, `<app-contacto-form` y `ancla="contacto"`.
- [ ] WHEN se lee `contacto-form.component.ts` THE SYSTEM SHALL declarar `enviado = output<{ nombre: string; correo: string; mensaje: string }>();`, usar `effect(` y precargar el texto `Me interesa el plan ${plan.nombre}.`.
- [ ] WHEN se lee `contacto-form.component.html` THE SYSTEM SHALL contener `¡Gracias! Te contactaremos pronto.`, `<app-campo-texto` y `<app-campo-error`.
- [ ] WHEN se buscan `HttpClient` y `fetch(` en `src/app` THE SYSTEM SHALL encontrar cero coincidencias.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/landing/landing.component.html && test "$(wc -l < src/app/pages/landing/landing.component.html)" -le 60  # expect: exit 0 (≤ 60 líneas)
f=src/app/pages/landing/landing.component.html; test -f $f && (for t in app-hero-carousel app-service-card app-plan-card app-team-card app-testimonio-card app-cta-banner app-contacto-form; do grep -qF "<$t" $f || exit 1; done) && grep -qF 'ancla="contacto"' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.ts; test -f $f && grep -qF 'enviado = output<{ nombre: string; correo: string; mensaje: string }>();' $f && grep -qF 'effect(' $f && grep -qF 'Me interesa el plan ${plan.nombre}.' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.html; test -f $f && grep -qF '¡Gracias! Te contactaremos pronto.' $f && grep -qF '<app-campo-texto' $f && grep -qF '<app-campo-error' $f  # expect: exit 0
test -d src/app && test -z "$(grep -rnE 'HttpClient|fetch\(' src/app)"  # expect: exit 0, nada se envía
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T4: Formulario de contacto con plan precargado (sin envío real)" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null || git tag dogtor-s09
git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s08`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s08 --staged --worktree -- src && git clean -fd -- src
```

---

#### Paso 10 — Gate final de integración de las correcciones

**Objetivo:** Después de este paso hay evidencia, en un solo bloque ejecutable, de que las tres correcciones conviven: todos los gates de los pasos 1–9 siguen en verde y nada fuera del alcance cambió frente a `dogtor-base`.  
**Tarea:** `E3-T5` (epic `03-landing-completa`) · **Depende de:** paso 9 · **Prioridad:** p0

**Do**

Este paso no escribe código de aplicación: ejecuta juntos todos los gates estáticos de los pasos 1–9 (que un paso posterior podría haber roto) más las comprobaciones que solo tienen sentido al final: build sin avisos de presupuesto de estilos, salida del build en `dist/dogtor-angular/browser/`, rutas / `styles.scss` / navbar / footer / `angular.json` / `package.json` / `package-lock.json` sin cambios frente a `dogtor-base`, ningún `*.spec.ts` y las etiquetas de los pasos anteriores presentes.

Si algo falla aquí, **no** se edita el gate: se vuelve al paso cuyo gate falla (su Rollback está en ese paso) y se corrige allí. El checklist visual manual de §20.1 (con `npx ng serve`) se recorre después de este paso y **no** es un gate.

Archivos tocados: ninguno de `src/`. El Checkpoint solo registra `tasks.json` (si cambió su `status`) y crea la etiqueta `dogtor-s10`.

##### Archivos

Ninguno en `src/`. Este paso solo ejecuta gates.

**Done when**

- [ ] WHEN se ejecuta `npx ng build` THE SYSTEM SHALL terminar con código 0 y su salida SHALL no contener la palabra `budget`.
- [ ] WHEN se re-ejecutan juntos los comandos `verify` de E1-T1 a E3-T4 THE SYSTEM SHALL terminar cada uno con código 0.
- [ ] WHEN se compara contra la etiqueta `dogtor-base` THE SYSTEM SHALL no mostrar cambios en `src/app/app.routes.ts`, `src/styles.scss`, `src/app/components/navbar`, `src/app/components/footer`, `angular.json`, `package.json` ni `package-lock.json`.
- [ ] WHEN se buscan archivos `*.spec.ts` en `src` THE SYSTEM SHALL encontrar cero.
- [ ] WHEN se consultan las etiquetas de git THE SYSTEM SHALL resolver `dogtor-base` y `dogtor-s01` a `dogtor-s09`.

**Verify** — desde `dogtor-angular/`; cada línea termina con exit 0 cuando el paso está bien. Ninguna depende del commit de este paso.

```bash
npx ng build > /tmp/dogtor-build.log 2>&1 && test -z "$(grep -i budget /tmp/dogtor-build.log)"  # expect: exit 0 y ningún aviso de presupuesto (anyComponentStyle 4kB/8kB)
test -f src/app/models/mascota.model.ts && grep -qF 'dueno?: Dueno;' src/app/models/mascota.model.ts && grep -qF "import { Dueno } from './dueno.model';" src/app/models/mascota.model.ts  # expect: exit 0
test "$(grep -n 'private duenoService = inject(DuenoService);' src/app/service/mascota.service.ts | cut -d: -f1)" -lt "$(grep -n 'private mascotaArray: Mascota\[\] = \[' src/app/service/mascota.service.ts | cut -d: -f1)"  # expect: exit 0 (duenoService se declara antes del arreglo)
a="$(git show dogtor-base:./src/app/service/mascota.service.ts | grep -oE 'duenoId: [0-9]+ \}' | grep -oE '[0-9]+')"; b="$(grep -oE 'getDuenoById\([0-9]+\) \}' src/app/service/mascota.service.ts | grep -oE '[0-9]+')"; test -n "$a" && test "$a" = "$b"  # expect: exit 0 (cada fila usa el mismo N que tenía en dogtor-base)
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts && test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && test -f src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -nE 'DuenoService|getDuenoById|getNombreDueno' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0, sin coincidencias
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF "{{ mascota.dueno?.nombre ?? '—' }}" src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rn duenoId src/app)"  # expect: exit 0, cero ocurrencias
test -d src/app && test -z "$(grep -rn getDuenoById src/app --include=*.ts | grep -v '^src/app/service/')"  # expect: exit 0, solo se usa dentro de src/app/service/
test -f src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: new FormControl<Dueno | null>(null, [Validators.required]),' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: mascota.dueno ?? null,' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: formValue.dueno ?? undefined,' src/app/pages/mascota-form/mascota-form.component.ts  # expect: exit 0
test -d src/app/pages/mascota-form && grep -rqF 'compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);' src/app/pages/mascota-form && grep -rqF '[compareWith]="compararDuenos"' src/app/pages/mascota-form && grep -rqF '<option [ngValue]="dueno">' src/app/pages/mascota-form  # expect: exit 0
test -f src/app/service/mascota.service.ts && grep -qF 'm.dueno?.id === dueno.id' src/app/service/mascota.service.ts  # expect: exit 0
(for c in estado-badge mascota-avatar; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 6 archivos existen
grep -qF 'activa = input.required<boolean>();' src/app/components/estado-badge/estado-badge.component.ts && grep -qF 'nombre = input.required<string>();' src/app/components/mascota-avatar/mascota-avatar.component.ts  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '<app-mascota-avatar' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '[tamano]="56"' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app/pages/mascota-detail && grep -rqF '[tamano]="180"' src/app/pages/mascota-detail && grep -rqF '<app-estado-badge [activa]=' src/app/pages/mascota-detail  # expect: exit 0
test -d src/app && test -z "$(grep -rln fotoPorDefecto src/app --include=*.ts | grep -v '^src/app/components/mascota-avatar/')"  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss && test -f src/app/pages/mascota-detail/mascota-detail.component.scss && test -z "$(grep -n '^\.foto' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss src/app/pages/mascota-detail/mascota-detail.component.scss)"  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.html && test "$(wc -l < src/app/pages/mascota-form/mascota-form.component.html)" -le 70  # expect: exit 0 (≤ 70 líneas)
(for c in campo-error campo-texto; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done; for c in dueno-select foto-preview; do for e in ts html scss; do test -f src/app/pages/mascota-form/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 12 archivos existen
grep -qF '<div class="invalid-feedback d-block">' src/app/components/campo-error/campo-error.component.html  # expect: exit 0
f=src/app/pages/mascota-form/mascota-form.component.html; test -f $f && grep -qF 'campoId="nombre"' $f && grep -qF 'campoId="raza"' $f && grep -qF 'campoId="edad"' $f && grep -qF 'campoId="fotoUrl"' $f && grep -qF 'campoId="vacunas"' $f && grep -qF '<app-foto-preview' $f && grep -qF '<app-dueno-select' $f  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.ts && test -f src/app/pages/mascota-form/mascota-form.component.html && test -z "$(grep -nE 'campoInvalido|invalid-feedback' src/app/pages/mascota-form/mascota-form.component.ts src/app/pages/mascota-form/mascota-form.component.html)"  # expect: exit 0
grep -qF '.preview {' src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss && test -f src/app/pages/mascota-form/mascota-form.component.scss && test -z "$(grep -n 'preview' src/app/pages/mascota-form/mascota-form.component.scss)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.html && test "$(wc -l < src/app/pages/mascota-detail/mascota-detail.component.html)" -le 45  # expect: exit 0 (≤ 45 líneas)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-mascota-info-card' src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-registro-medico-item' src/app/pages/mascota-detail/mascota-detail.component.html && test -z "$(grep -n 'getNombre' src/app/pages/mascota-detail/mascota-detail.component.html)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.ts && grep -qF 'registros: RegistroVista[] = [];' src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -n 'DuenoService' src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0
f=src/app/components/dueno-card/dueno-card.component; test -f $f.html && grep -qF 'Sin dueño asignado' $f.html && grep -qF 'dueno = input<Dueno | undefined>();' $f.ts  # expect: exit 0
f=src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts; test -f $f && grep -qF 'registro = input.required<RegistroMedico>();' $f && grep -qF 'veterinario = input.required<string>();' $f && grep -qF 'drogas = input<string[]>([]);' $f && test -z "$(grep -n 'inject(' $f)"  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF "providedIn: 'root'" $f && grep -qF 'getSlides()' $f && grep -qF 'getServicios()' $f && grep -qF 'getPlanes()' $f && grep -qF 'getEquipo()' $f && grep -qF 'getTestimonios()' $f && grep -qF 'getContacto()' $f  # expect: exit 0
f=src/app/service/landing.service.ts; grep -qF 'photo-1552053831-71594a27632d?w=1200' $f && grep -qF 'photo-1537151608804-ea6f117f73d2?w=1200' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.ts && test -z "$(grep -n unsplash src/app/pages/landing/landing.component.ts)"  # expect: exit 0
grep -qF 'slides = input.required<Slide[]>();' src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts && grep -qF '<app-hero-carousel [slides]="slides">' src/app/pages/landing/landing.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rni whatsapp src/app)"  # expect: exit 0, cero coincidencias
f=src/app/pages/landing/components/plan-card/plan-card.component.ts; test -f $f && grep -qF 'plan = input.required<Plan>();' $f && grep -qF 'planElegido = output<Plan>();' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-section-header' $f && grep -qF '<app-service-card' $f && grep -qF '(planElegido)="elegirPlan($event)"' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.ts; test -f $f && grep -qF "document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });" $f && grep -qF 'planSeleccionado: Plan | undefined;' $f  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && (for i in bi-heart-pulse bi-shield-check bi-scissors bi-capsule bi-house-heart bi-droplet; do grep -qF "'$i'" $f || exit 1; done)  # expect: exit 0, los 6 iconos
f=src/app/service/landing.service.ts; grep -qF "precio: '\$49.900'" $f && grep -qF "precio: '\$89.900'" $f && grep -qF "precio: '\$149.900'" $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-team-card' $f && grep -qF '<app-testimonio-card' $f && grep -qF '<app-cta-banner' $f && grep -qF 'ruta="/mascotas"' $f  # expect: exit 0
grep -qF 'bi-person-circle' src/app/pages/landing/components/team-card/team-card.component.html && grep -qF 'bi-star-fill' src/app/pages/landing/components/testimonio-card/testimonio-card.component.html  # expect: exit 0
grep -qF 'ruta = input.required<string>();' src/app/pages/landing/components/cta-banner/cta-banner.component.ts && grep -qF '[routerLink]="ruta()"' src/app/pages/landing/components/cta-banner/cta-banner.component.html  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF 'Dr. Andres Felipe' $f && grep -qF 'Dra. Laura Jimenez' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.html && test "$(wc -l < src/app/pages/landing/landing.component.html)" -le 60  # expect: exit 0 (≤ 60 líneas)
f=src/app/pages/landing/landing.component.html; test -f $f && (for t in app-hero-carousel app-service-card app-plan-card app-team-card app-testimonio-card app-cta-banner app-contacto-form; do grep -qF "<$t" $f || exit 1; done) && grep -qF 'ancla="contacto"' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.ts; test -f $f && grep -qF 'enviado = output<{ nombre: string; correo: string; mensaje: string }>();' $f && grep -qF 'effect(' $f && grep -qF 'Me interesa el plan ${plan.nombre}.' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.html; test -f $f && grep -qF '¡Gracias! Te contactaremos pronto.' $f && grep -qF '<app-campo-texto' $f && grep -qF '<app-campo-error' $f  # expect: exit 0
test -d src/app && test -z "$(grep -rnE 'HttpClient|fetch\(' src/app)"  # expect: exit 0, nada se envía
test -f dist/dogtor-angular/browser/index.html && grep -qF '<app-root>' dist/dogtor-angular/browser/index.html  # expect: exit 0, el build quedó en el outputPath de angular.json
git diff --quiet dogtor-base -- src/app/app.routes.ts src/styles.scss src/app/components/navbar src/app/components/footer angular.json package.json package-lock.json  # expect: exit 0, sin cambios frente a dogtor-base
test -d src && test -z "$(find src -name '*.spec.ts')"  # expect: exit 0, no se agregó ningún *.spec.ts
git rev-parse -q --verify refs/tags/dogtor-base >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null  # expect: exit 0, existen dogtor-base y dogtor-s01…dogtor-s09
```

**Checkpoint** — solo después de que la última línea de Verify termine con exit 0.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T5: Gate final de integración de las correcciones" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s10 >/dev/null || git tag dogtor-s10
git rev-parse -q --verify refs/tags/dogtor-s10 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — si este paso sale mal, vuelve al estado verificado anterior (`dogtor-s09`) sin tocar nada fuera de `src/`:

```bash
git restore --source=dogtor-s09 --staged --worktree -- src && git clean -fd -- src
```

---


### 9.1 Parity and cutover

NOT APPLICABLE — este cambio no es una migración: no reemplaza ningún sistema en ejecución, no
cambia de framework, de base de datos ni de proveedor, y no hay datos que mover. Es un cambio
incremental dentro de la misma app Angular, con un estado intermedio coexistente (paso 1) que se
cierra en el paso siguiente (paso 2), y cada paso tiene su propio rollback por etiqueta.

---

## 10. Configuración del entorno (Environment Setup)

### Prerequisites

| Tool | Version | Check |
|---|---|---|
| Node.js | v24.14.1 en esta máquina; `@angular/core` 19.2.25 declara `^18.19.1 \|\| ^20.11.1 \|\| >=22.0.0` | `node -v` |
| npm | 11.11.0 en esta máquina | `npm -v` |
| Git | 2.53.0 en esta máquina (cualquiera ≥ 2.23 tiene `git switch` y `git restore`) | `git --version` |
| Git Bash (GNU coreutils) | el que trae Git for Windows | `bash --version` |

### Accounts to create first

Ninguna. El cambio no usa servicios externos.

### Environment variables

| Variable | Purpose | Where to get it | Required by step | Secret? |
|---|---|---|---|---|
| — (ninguna) | El proyecto no lee variables de entorno ni archivos `.env` | — | — | — |

No hay `.env` ni `.env.example`: ningún comando de este blueprint lee variables de entorno, así que
no hay validación de entorno que pueda romper un gate anterior.

### Files that must be committed

Comprobado el 2026-10-01 con `git check-ignore` desde `dogtor-angular/` (incluye el `.gitignore`
del proyecto, los del repo padre y el global): ninguno de estos archivos coincide con un patrón de
ignorado, así que no hace falta ninguna línea de excepción.

| File | Why it is committed | Ignore-file exception line |
|---|---|---|
| `package-lock.json` | `npm ci` lo necesita en un checkout limpio | — no lo cubre ningún patrón |
| `CLAUDE.md`, `AGENTS.md` | Instrucciones del builder (§19.1, §19.2) | — no lo cubre ningún patrón |
| `.claude/settings.json`, `.claude/skills/verificar-cambio/SKILL.md`, `.claude/rules/angular-componentes.md` | Permisos, skill y reglas del builder (§19.3–§19.5) | — no lo cubre ningún patrón |
| `blueprints/correcciones-profe/**` | El bundle y el estado de `tasks.json` | — no lo cubre ningún patrón |
| `src/**` | El código del cambio | — no lo cubre ningún patrón |

El `.gitignore` del proyecto ya existe en el repo (no lo entrega ningún paso) e ignora
`/node_modules`, `/dist` y `/.angular/cache`; por eso `npm ci` y `npx ng build` pueden correr antes o
después del commit base sin que su salida entre en ningún commit.

### Bootstrap

Se ejecuta **una vez** antes del paso 1, desde `dogtor-angular/`, en Git Bash. No es interactivo y
es seguro correrlo dos veces seguidas: cada línea termina con exit 0 también en la segunda corrida.

```bash
# order matters: .gitignore (ya existe en el repo) → npm ci → copia de workspace/ → rama → commit base + etiqueta → build
npm ci   # instala exactamente el lockfile; no modifica package.json ni package-lock.json
cp -R blueprints/correcciones-profe/workspace/. .   # copia con sobrescritura: workspace/ solo trae CLAUDE.md, AGENTS.md y .claude/**, que ningún paso edita; package.json y package-lock.json no están en workspace/ y nunca se pisan
git switch feature/angular-correcciones 2>/dev/null || git switch -c feature/angular-correcciones   # crea o reutiliza la rama; los cambios sin commit del repo padre viajan intactos
git rev-parse -q --verify refs/tags/dogtor-base >/dev/null || { git add -A -- . && { git diff --cached --quiet -- . || git commit -m "chore(dogtor-angular): baseline antes de correcciones del profe" -- . ; } && git tag dogtor-base; }   # commit base SOLO de dogtor-angular/ (pathspec), una sola vez: si dogtor-base ya existe no vuelve a commitear nada
npx ng build   # baseline: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado)
```

Por qué cada línea es segura de repetir:

| Línea | Segunda corrida | Exit |
|---|---|---|
| `npm ci` | reinstala lo mismo | 0 |
| `cp -R …/workspace/. .` | sobrescribe con bytes idénticos (GNU `cp` de Git Bash; en macOS el `cp` de BSD también sale con 0 al sobrescribir) | 0 |
| `git switch …` | "Already on 'feature/angular-correcciones'" | 0 |
| commit base + etiqueta | `dogtor-base` ya existe → no hace nada (y no arrastra trabajo de pasos posteriores a un "baseline") | 0 |
| `npx ng build` | recompila | 0 |

La primera corrida crea el commit base con todo `dogtor-angular/` salvo lo ignorado, incluidos
`CLAUDE.md`, `AGENTS.md`, `.claude/**` y este bundle, y la etiqueta `dogtor-base` a la que apuntan el
rollback del paso 1 y las comprobaciones de §20.1. El repositorio ya existe (es el repo padre), así
que no hace falta `git init`; si el commit con pathspec no encuentra cambios (por ejemplo porque
alguien ya había commiteado `dogtor-angular/`), la etiqueta se pone sobre el `HEAD` actual.
**Nunca** se usa `git add -A` en la raíz del repo padre ni `git commit -a`.

---

## 11. Dependencias (Dependencies)

**Nada se instala, agrega ni actualiza en este cambio.** Todas las versiones salen del
`package-lock.json` del repo y las instala la línea `npm ci` del Bootstrap de §10. Los paquetes con
URL del registro fueron verificados por el hilo principal el 2026-10-01 con
`npm view <paquete>@<versión> version` (19.2.25 y 19.2.27 son las últimas de la línea 19.x de Angular).
Los marcados como "lockfile" no se re-verificaron contra el registro: su versión es la resuelta en el
lockfile y se instala tal cual.

### Runtime

| Package | Version | Source (registry URL or track file) | Checked | Installed by | Purpose |
|---|---|---|---|---|---|
| `@angular/core` | 19.2.25 | https://registry.npmjs.org/@angular/core | 2026-10-01 | §10 Bootstrap (`npm ci`) | Componentes, `input()`, `output()`, `effect()`, `inject()` |
| `@angular/common` | 19.2.25 | https://registry.npmjs.org/@angular/core (misma versión del framework, verificada con el resto de @angular/*) | 2026-10-01 | §10 Bootstrap (`npm ci`) | `DatePipe` en `registro-medico-item` |
| `@angular/compiler` | 19.2.25 | https://registry.npmjs.org/@angular/core (ídem) | 2026-10-01 | §10 Bootstrap (`npm ci`) | Compilador de plantillas en tiempo de build |
| `@angular/forms` | 19.2.25 | https://registry.npmjs.org/@angular/core (ídem) | 2026-10-01 | §10 Bootstrap (`npm ci`) | `ReactiveFormsModule`, `FormControl`, `Validators`, `compareWith` |
| `@angular/router` | 19.2.25 | https://registry.npmjs.org/@angular/core (ídem) | 2026-10-01 | §10 Bootstrap (`npm ci`) | `RouterLink` en tarjetas y banner |
| `@angular/platform-browser` | 19.2.25 | https://registry.npmjs.org/@angular/core (ídem) | 2026-10-01 | §10 Bootstrap (`npm ci`) | `bootstrapApplication` (sin cambios) |
| `@angular/platform-browser-dynamic` | 19.2.25 | https://registry.npmjs.org/@angular/core (ídem) | 2026-10-01 | §10 Bootstrap (`npm ci`) | Dependencia existente del proyecto (sin uso nuevo) |
| `bootstrap` | 5.3.8 | https://registry.npmjs.org/bootstrap | 2026-10-01 | §10 Bootstrap (`npm ci`) | Estilos y grilla; cargado en `angular.json` |
| `bootstrap-icons` | 1.13.1 | https://registry.npmjs.org/bootstrap-icons | 2026-10-01 | §10 Bootstrap (`npm ci`) | Iconos `bi-*` (todos los usados existen en esta versión; comprobado en su CSS el 2026-10-01) |
| `rxjs` | 7.8.2 | package-lock.json del repo (rango `~7.8.0`); UNVERIFIED en el registro, no se instala nada nuevo | 2026-10-01 | §10 Bootstrap (`npm ci`) | Dependencia de Angular (sin uso directo nuevo) |
| `zone.js` | 0.15.1 | package-lock.json del repo (rango `~0.15.0`); UNVERIFIED en el registro, no se instala nada nuevo | 2026-10-01 | §10 Bootstrap (`npm ci`) | Detección de cambios (polyfill en `angular.json`) |
| `tslib` | 2.8.1 | package-lock.json del repo (rango `^2.3.0`); UNVERIFIED en el registro, no se instala nada nuevo | 2026-10-01 | §10 Bootstrap (`npm ci`) | Helpers de TypeScript (`importHelpers`) |

### Development

| Package | Version | Source (registry URL or track file) | Checked | Installed by | Purpose |
|---|---|---|---|---|---|
| `@angular/cli` | 19.2.27 | https://registry.npmjs.org/@angular/cli | 2026-10-01 | §10 Bootstrap (`npm ci`) | `npx ng build` / `npx ng serve` |
| `@angular-devkit/build-angular` | 19.2.27 | https://registry.npmjs.org/@angular/cli (misma línea de herramientas, verificada por el hilo principal) | 2026-10-01 | §10 Bootstrap (`npm ci`) | Builder `application` usado por `ng build` |
| `@angular/compiler-cli` | 19.2.25 | package-lock.json del repo (misma versión que `@angular/core`); UNVERIFIED en el registro por separado | 2026-10-01 | §10 Bootstrap (`npm ci`) | Compilación AOT con `strictTemplates` |
| `typescript` | 5.7.3 | https://registry.npmjs.org/typescript | 2026-10-01 | §10 Bootstrap (`npm ci`) | Compilador |
| `karma`, `karma-*`, `jasmine-core`, `@types/jasmine` | 6.4.4 / lockfile | package-lock.json del repo; UNVERIFIED en el registro | 2026-10-01 | §10 Bootstrap (`npm ci`) | Ya están en `devDependencies`; **no se usan** (no hay tests, §13). Se instalan solo porque `npm ci` instala el lockfile completo |

| Prerrequisito (no lo instala ningún paso) | Version | Source | Checked |
|---|---|---|---|
| Node.js | v24.14.1 | máquina del usuario (`node -v`) | 2026-10-01 |

### Deliberately not used

| Rejected | Instead | Why |
|---|---|---|
| ESLint, Prettier, Biome | `.editorconfig` + revisión de los cuerpos literales | Convención del repo; agregar un linter es un No-objetivo |
| Jest, Vitest, correr Karma | `npx ng build` (AOT + `strictTemplates`) y gates estáticos | El repo no tiene tests (decisión en §20.3) |
| `HttpClient` / `fetch` | Nada: el formulario de contacto no envía | No hay backend; el gate del paso 9 lo comprueba |
| ng-bootstrap, ngx-bootstrap, Angular Material | Componentes propios con clases de Bootstrap | Serían dependencias nuevas |
| `registerLocaleData` + `CurrencyPipe` para COP | Precios ya formateados como texto (`'$49.900'`) | Evita configurar el locale `es-CO` para 3 precios fijos |

---

## 12. Estrategia de despliegue (Deployment Strategy)

### Hosting

NOT APPLICABLE — la app no se despliega; se demuestra con `npx ng serve` en http://localhost:4200.
El build de producción (`npx ng build`) queda en `dist/dogtor-angular/browser/` solo como gate.

### Environments

| Environment | Branch | URL | Database | Third-party mode |
|---|---|---|---|---|
| Local | `feature/angular-correcciones` | http://localhost:4200 (`npx ng serve`) | ninguna (arreglos en memoria) | ninguno |
| Preview | NOT APPLICABLE — no hay entorno de preview | — | — | — |
| Production | NOT APPLICABLE — no hay despliegue | — | — | — |

### CI/CD

NOT APPLICABLE — el repo no tiene pipeline. El mismo gate que correría un CI es §20.1, que se
ejecuta a mano o con el skill `verificar-cambio` (§19.4).

### Release and rollback

La "entrega" es el commit de la etiqueta `dogtor-s10` en la rama `feature/angular-correcciones`
(integrarla a `main` lo decide el usuario después; no es un paso del build). Rollback:

| Alcance | Comando (desde `dogtor-angular/`) | Tiempo |
|---|---|---|
| Deshacer un paso N que salió mal | `git restore --source=<etiqueta N-1> --staged --worktree -- src && git clean -fd -- src` (cada paso trae el suyo literal) | segundos |
| Deshacer todo el cambio | `git restore --source=dogtor-base --staged --worktree -- src && git clean -fd -- src` | segundos |

Nunca `git reset --hard`, `git stash`, `git checkout -- .` ni `git clean` sin `-- src`: el árbol del
repo padre tiene cambios sin commit de `Proyecto-Veterinaria-2.0/`. No hay migraciones de datos, así
que no hay orden de despliegue que respetar.

### Domain, DNS, TLS

NOT APPLICABLE — no hay dominio ni despliegue.

---

## 13. Estrategia de pruebas (Testing Strategy)

La convención del repo es **sin tests** (cero `*.spec.ts`, `skipTests: true` en todos los
schematics) y este cambio la respeta (decisión en §20.3). Los "Done when" de §9 se deciden con:

| Layer | Framework | What it covers | Where | Runs |
|---|---|---|---|---|
| Compilación | `npx ng build` (AOT, `strict`, `strictTemplates`) | Tipos de modelos, de entradas/salidas de componentes y de cada binding de plantilla; presupuestos de estilos | todo `src/` | cada paso |
| Gates estáticos | `test`, `grep`, `wc`, `find`, `git diff` | Estructura: archivos creados, usos, ausencias (`duenoId`, WhatsApp), límites de líneas, interfaces congeladas | arreglos `verify` de cada tarea | cada paso; todos juntos en el paso 10 |
| Unit | NOT APPLICABLE — sin runner por convención del repo | — | — | — |
| Integration | NOT APPLICABLE — sin backend | — | — | — |
| E2E | NOT APPLICABLE — sin runner; se reemplaza por el checklist visual manual de §20.1 | — | — | — |

### Critical flows to cover E2E

Se recorren a mano con `npx ng serve` (checklist de §20.1, no es un gate):

1. Editar una mascota existente: el `<select>` muestra seleccionado su dueño actual; guardar conserva el dueño en la tabla y en el detalle.
2. Elegir un plan en la landing: la página baja al contacto y el mensaje dice "Me interesa el plan <nombre>.".
3. Enviar el formulario de contacto vacío muestra los errores; enviarlo válido muestra "¡Gracias! Te contactaremos pronto.".

### Test data

Las semillas de los servicios; cada recarga (F5) vuelve al mismo estado, así que los recorridos
manuales no comparten estado entre sí.

### What is deliberately not tested

El comportamiento en ejecución (navegación, `compareWith`, `effect()`, scroll) no tiene prueba
automática: no hay runner y agregarlo es un No-objetivo. Se cubre con el checklist manual de §20.1.

---

## 14. Seguridad y secretos (Security & Secrets)

| Concern | Control | Implemented in |
|---|---|---|
| Secret storage | No hay secretos: no hay API keys, tokens ni `.env` | — |
| Secret rotation | NOT APPLICABLE — no hay secretos | — |
| Input validation | `Validators` de Angular en los formularios de mascota y contacto | `mascota-form.component.ts`, `contacto-form.component.ts` |
| Output encoding / XSS | Interpolación `{{ }}` de Angular (escapa por defecto); ningún `innerHTML` ni `bypassSecurityTrust*` | todas las plantillas |
| SQL injection | NOT APPLICABLE — no hay base de datos | — |
| AuthN / AuthZ | NOT APPLICABLE — ver §8 | — |
| CSRF | NOT APPLICABLE — no hay peticiones al servidor | — |
| Rate limiting / abuse | NOT APPLICABLE — no hay servidor | — |
| Webhook verification | NOT APPLICABLE | — |
| Dependency audit | Sin cambios de dependencias en este cambio; `npm audit` queda a criterio del usuario | — |
| Security headers | NOT APPLICABLE — no hay servidor propio (solo `ng serve` local) | — |
| PII handling | Lo escrito en el formulario de contacto vive solo en la memoria del componente y se borra con `reset()`; no se guarda ni se envía | `contacto-form.component.ts` |
| Logging hygiene | Ningún `console.log` de datos de formularios | todo `src/app` |

**Hard rules**
- No secret is ever committed, printed in a log, sent to an error tracker, or embedded in a
  client bundle. Anything reaching the browser is public — treat it that way.
- No se agrega ninguna llamada de red (el gate del paso 9 busca `HttpClient` y `fetch(`).
- Los enlaces a sitios externos que existían (WhatsApp) se retiran; no se agregan nuevos.

El proyecto no maneja datos regulados: las semillas son ficticias y el formulario de contacto no persiste nada.

---

## 15. Accesibilidad (Accessibility)

**Target: WCAG 2.2 Level AA** para todo lo nuevo, sin rehacer lo existente (la paleta congelada
tiene dos pares que no pasan, ver §7).

### Baseline requirements

| Requirement | Rule |
|---|---|
| Semantic HTML | Cada sección de la landing es un `<section>` con un `h2` (vía `app-section-header`); tarjetas con `h3`; el detalle conserva un único `h1` |
| Keyboard | Todo lo interactivo nuevo es `<button type="button">`, `<a routerLink>` o control de formulario nativo |
| Focus visible | El `:focus-visible` de `styles.scss` (contorno #f4a261 de 2 px) aplica sin cambios |
| Contrast | Ver §7; no se agregan pares nuevos de texto sobre #f4a261 fuera de botones existentes |
| Forms | Cada campo tiene `<label for>` que apunta a su control (`campoId`, `dueno`, `contacto-mensaje`); los errores son texto en `invalid-feedback`, no solo color |
| Images | Avatar con `alt` = nombre de la mascota; vista previa con `alt="Vista previa"`; diapositivas con `alt` = título; iconos decorativos sin texto alternativo |
| Motion | Sin animaciones nuevas (ver §7) |
| Zoom / reflow | Grillas `col-md-6 col-lg-4` de Bootstrap: una columna en pantallas angostas |
| Live regions | El mensaje de contacto enviado usa `role="status"`; las estrellas tienen `aria-label="N de 5 estrellas"` |

### WCAG 2.2 additions — the ones most often missed

| SC | Requirement |
|---|---|
| 2.4.11 Focus Not Obscured (Min) | El navbar es `sticky-top`; `app-section-header` usa `scroll-margin-top: 6rem` para que el encabezado de destino no quede debajo |
| 2.5.7 Dragging Movements | No hay interacciones de arrastre |
| 2.5.8 Target Size (Min) | Botones de Bootstrap (≥ 38 px de alto); los indicadores del carrusel (12 px) existían antes y no se modifican |
| 3.3.7 Redundant Entry | Elegir un plan precarga el mensaje del formulario de contacto |
| 3.3.8 Accessible Authentication (Min) | NOT APPLICABLE — no hay autenticación |

### Verification

```bash
npx ng build   # expect: exit 0 — strictTemplates rechaza bindings inválidos de label/control
```
No se agrega herramienta automática de accesibilidad (sería una dependencia nueva). Antes de la
entrega: recorrido solo con teclado de la landing y del formulario de mascota, y una pasada con
zoom al 200 % en ancho de móvil (checklist manual de §20.1).

---

## 16. Observabilidad y costo (Observability & Cost)

NOT APPLICABLE — es una app local de curso sin despliegue, sin servidor y sin servicios pagos: no
hay errores remotos que rastrear, métricas que recoger ni costo mensual (USD 0). Ningún paso de §9
depende de esta sección.

---

## 17. Model Routing

NOT APPLICABLE — this project does not call an LLM at runtime.

---

## 18. Skills para el build (Skills to Use During Build)

Ningún paso depende de un skill: si no está disponible, el builder sigue el contenido literal de §9
y anota en una línea que no lo usó.

| Skill | Build steps | Why | Install |
|---|---|---|---|
| `frontend-design` (se activa solo; sin barra) | 6, 7, 8, 9 | Criterio visual para las secciones nuevas de la landing, sin salirse de los cuerpos literales de §9 | `/plugin marketplace add anthropics/skills` y luego `/plugin install example-skills@anthropic-agent-skills` |
| `verificar-cambio` (skill del proyecto, se activa solo) | 1–10 | Corre el gate completo de §20.1 antes de marcar una tarea `done` | Ya viene en el bundle: `workspace/.claude/skills/verificar-cambio/SKILL.md` llega a `.claude/skills/` con la copia del Bootstrap de §10 |

---

## 19. Espacio de trabajo del agente (Agent Workspace)

`workspace/` refleja la raíz del proyecto: el Bootstrap de §10 la copia **una vez** con
`cp -R blueprints/correcciones-profe/workspace/. .` antes del paso 1 y antes del commit base.

```
blueprints/correcciones-profe/workspace/
├── CLAUDE.md                                 # §19.1
├── AGENTS.md                                 # §19.2
└── .claude/
    ├── settings.json                         # §19.3
    ├── skills/verificar-cambio/SKILL.md      # §19.4
    └── rules/angular-componentes.md          # §19.5
```

**La copia es segura de repetir.** Se usa la forma con sobrescritura (sin `-n`) porque `workspace/`
no contiene ningún archivo que un paso edite después: ni `package.json` ni `package-lock.json` (que
ya existen en el repo y nunca se emiten aquí), ni código de `src/`. Repetirla escribe los mismos bytes
y sale con exit 0 (GNU `cp` de Git Bash). **Archivos que esta copia nunca pisa:** `package.json`,
`package-lock.json` y todo `src/`.

**Si en el futuro el repo ya tiene `CLAUDE.md` o `AGENTS.md`, su contenido se fusiona con el de
`workspace/` a mano; no se sobrescribe.** Hoy no existen, por eso la copia es directa.

No se emite `.claude/commands/`. Todos los archivos usan 2 espacios, UTF-8 y salto de línea final
(la `.editorconfig` del repo, que es la única configuración de formato que existe).

### 19.1 `CLAUDE.md`

```markdown
# DogTor (Angular)

SPA de la clínica veterinaria DogTor en Angular 19 standalone: landing pública y CRUD de mascotas
con datos quemados en servicios (no hay backend). Proyecto de curso.

<!-- Generado por The Architect (blueprints/correcciones-profe/). Si en el futuro el repo ya tiene
     un CLAUDE.md, este contenido se FUSIONA con el existente; nunca se sobrescribe. -->

## Comandos

Todo se ejecuta desde `dogtor-angular/` (la carpeta que contiene `package.json`). Shell: Git Bash.

| Tarea | Comando |
|---|---|
| Instalar (lockfile) | `npm ci` |
| Build (gate principal) | `npx ng build` — exit 0; el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado |
| Servidor de desarrollo | `npx ng serve` — http://localhost:4200 |
| Gate completo | el skill `verificar-cambio` (corre el gate de §20.1 del blueprint) |
| Siguiente tarea | `blueprints/correcciones-profe/tasks.json` + el epic de `blueprints/correcciones-profe/epics/` |

**Gate:** `npx ng build` más los comandos `verify` de la tarea, todos con exit 0, antes de marcar
una tarea como `done`. No hay test runner, linter ni formatter en este repo y **no se agregan**:
los gates son `npx ng build` (compila AOT con `strictTemplates`) y comprobaciones con `test`,
`grep`, `wc` y `find`.

Versiones: las del `package-lock.json` (Angular 19.2.x, Bootstrap 5.3.8, bootstrap-icons 1.13.1,
TypeScript 5.7.3). Nunca se agregan ni actualizan dependencias en este cambio.

## Stack

Angular 19 standalone (sin NgModules) · TypeScript strict + `strictTemplates` · Bootstrap 5.3 +
bootstrap-icons (cargados en `angular.json`) · SCSS por componente · zone.js · datos en memoria.

## Arquitectura

**Flujo de datos.** Ruta (`src/app/app.routes.ts`) → página en `src/app/pages/<pagina>/` → servicio
`providedIn: 'root'` en `src/app/service/` que tiene un arreglo quemado → componentes hijos que
reciben datos con `input()` y avisan eventos con `output()`. Al recargar (F5) los datos vuelven a la
semilla: no hay persistencia.

**Relaciones.** `Mascota.dueno` es un objeto `Dueno` (la misma instancia que guarda `DuenoService`),
no un id. Los componentes nunca resuelven ids de dueño. `RegistroMedico`, `Dueno.usuarioId`,
`Administrador.usuarioId` y `Veterinario.usuarioId` siguen con ids a propósito (cambio posterior).

| Capa | Puede importar de | Nunca |
|---|---|---|
| `src/app/pages/**` | `components`, `service`, `models` | otra página |
| `src/app/pages/<p>/components/**` | `components`, `models` | servicios (son presentacionales) |
| `src/app/components/**` | `models`, otros `components` | servicios, páginas |
| `src/app/service/**` | `models`, otros servicios | componentes |

**Dónde vive cada cosa.**

| Tema | Única fuente |
|---|---|
| Rutas | `src/app/app.routes.ts` — no se cambia |
| Paleta y utilidades de marca | `src/styles.scss` — no se cambia |
| Datos de la landing | `src/app/service/landing.service.ts` + `src/app/models/landing.model.ts` |
| Imagen por defecto de mascota | `src/app/components/mascota-avatar/` |
| Mensajes de error de formularios | `src/app/components/campo-error/` |

## Reglas de código

1. Componentes standalone con `templateUrl` + `styleUrl` (`.scss`) e `imports: []` explícito.
   Archivos `<nombre>.component.{ts,html,scss}`, selector `app-<nombre>`, clase `<Nombre>Component`.
2. Entradas con `input()` / `input.required()` y salidas con `output()`; nunca `@Input`/`@Output`.
3. Control de flujo nuevo: `@if`, `@for (...; track ...)`, `@empty`. Nunca `*ngIf` ni `*ngFor`.
4. Inyección con `inject()` en campos, con el comentario `//DI` encima. Un campo que otro
   inicializador usa se declara antes (los inicializadores corren en orden).
5. Subcomponente usado por una sola página → `src/app/pages/<pagina>/components/<nombre>/`.
   Compartido → `src/app/components/<nombre>/`.
6. Identificadores y comentarios en español; comentarios cortos que explican el porqué.
7. Nunca llamar desde el template a un método que devuelva un arreglo u objeto nuevo: se calcula
   una vez (en `ngOnInit` o en un campo) para evitar NG0100.
8. Estilos: utilidades de Bootstrap primero; cada `.scss` de componente muy pequeño (presupuesto
   `anyComponentStyle`: aviso 4 kB, error 8 kB).
9. Indentación de 2 espacios, comillas simples en TS, salto de línea final (`.editorconfig`).

## Diseño

Colores de `src/styles.scss`: `--dogtor-primary` #2a9d8f, `--dogtor-secondary` #f4a261,
`--dogtor-accent` #e9c46a, `--dogtor-bglight` #fafafa, `--dogtor-textdark` #264653. Clases:
`text-dogtor`, `bg-dogtor-dark`, `btn-dogtor`, `btn-dogtor-secondary`, `btn-dogtor-accent`.
No se agregan colores nuevos.

## Entorno

Este proyecto no usa variables de entorno ni `.env`.

## Reglas por área

| Archivo | Aplica a |
|---|---|
| `.claude/rules/angular-componentes.md` | `src/app/**` |

## Git — este proyecto vive dentro de otro repositorio

`dogtor-angular/` está dentro del repo de `C:/Users/samue/Universitat/Desktop/2630/Web`, cuyo árbol
tiene cambios sin commit de `Proyecto-Veterinaria-2.0/` que **no son de este trabajo**.

| Acción | Comando (siempre con pathspec) |
|---|---|
| Checkpoint de una tarea | `git add -A -- src blueprints`, luego `git diff --cached --quiet -- src blueprints \|\| git commit -m "<id>: <título>" -- src blueprints`, luego la etiqueta `dogtor-sNN` |
| Rollback de la tarea N | `git restore --source=dogtor-s(N-1) --staged --worktree -- src && git clean -fd -- src` (para N = 1 la fuente es `dogtor-base`) |

Rama de trabajo: `feature/angular-correcciones`. Etiquetas: `dogtor-base`, `dogtor-s01` … `dogtor-s10`.

## No negociable

1. **Nunca** `git reset --hard`, `git stash`, `git checkout -- .`, `git clean` sin `-- src`,
   `git add -A` sin pathspec ni `git commit -a`: destruyen o barren el trabajo sin commit de
   `Proyecto-Veterinaria-2.0/`.
2. Nunca tocar nada fuera de `dogtor-angular/`.
3. No cambiar rutas, `src/styles.scss`, navbar, footer, `angular.json`, `package.json` ni
   `package-lock.json`.
4. No agregar dependencias, tests, linters, formatters, backend ni `HttpClient`.
5. No editar un comando `verify`: si falla, se corrige el código del paso.
6. Nunca marcar una tarea `done` con un gate en rojo.
```

### 19.2 `AGENTS.md`

```markdown
# DogTor (Angular) — instrucciones para agentes

SPA de la clínica veterinaria DogTor en Angular 19 standalone, con datos quemados en servicios
(sin backend). La fuente de verdad completa es `CLAUDE.md`; este archivo es el resumen neutral.

## Comandos (desde `dogtor-angular/`, Git Bash)

| Tarea | Comando |
|---|---|
| Instalar | `npm ci` |
| Build (gate) | `npx ng build` — exit 0 |
| Servidor de desarrollo | `npx ng serve` — http://localhost:4200 |
| Tareas pendientes | `blueprints/correcciones-profe/tasks.json` y `blueprints/correcciones-profe/epics/` |

No hay tests ni linter: los gates son `npx ng build` y los comandos `verify` de cada tarea.

## Las reglas que más importan

1. Git siempre con pathspec (`-- src blueprints`). Nunca `git reset --hard`, `git stash`,
   `git add -A` sin pathspec ni `git commit -a`: el repo padre tiene trabajo sin commit de
   `Proyecto-Veterinaria-2.0/` que no se puede tocar.
2. Componentes standalone con `input()`/`output()`, `@if`/`@for`, `inject()` con `//DI`,
   identificadores en español. `Mascota.dueno` es un objeto, no un id.
3. No cambiar rutas, `src/styles.scss`, navbar, footer ni dependencias; no agregar tests,
   linters, backend ni `HttpClient`.

Rollback de la tarea N: `git restore --source=dogtor-s(N-1) --staged --worktree -- src && git clean -fd -- src`.
```

### 19.3 `.claude/settings.json`

Cubre cada comando de §10, de cada `Verify`, de cada `Checkpoint`, de cada Rollback y de §20.1:
`npm ci`, `npx ng build`, `npx ng serve`, la copia de `workspace/`, `test`, `grep`, `wc`, `cut`,
`find`, los bucles `for`/`(for`, las asignaciones `a=`/`f=` que abren algunos gates, `git show`,
`git diff`, `git rev-parse`, `git switch`, `git add` con pathspec, `git commit`, `git tag`,
`git restore --source=`, `git clean -fd -- src`, `git ls-files` y `git check-ignore`. Niega
`git reset`, `git stash`, `git push`, `git checkout -- .`, `git commit -a`, `git add` sin pathspec,
`git clean -fdx` y cualquier `npm install`.

```json
{
  "permissions": {
    "allow": [
      "Bash(npm ci:*)",
      "Bash(npx ng build:*)",
      "Bash(npx ng serve:*)",
      "Bash(cp -R blueprints/correcciones-profe/workspace/. .)",
      "Bash(test:*)",
      "Bash(grep:*)",
      "Bash(wc:*)",
      "Bash(cut:*)",
      "Bash(find:*)",
      "Bash(for:*)",
      "Bash((for:*)",
      "Bash(a=:*)",
      "Bash(f=:*)",
      "Bash(git status:*)",
      "Bash(git log:*)",
      "Bash(git show:*)",
      "Bash(git diff:*)",
      "Bash(git rev-parse:*)",
      "Bash(git switch:*)",
      "Bash(git add -A -- .)",
      "Bash(git add -A -- src blueprints)",
      "Bash(git commit:*)",
      "Bash(git tag:*)",
      "Bash(git ls-files:*)",
      "Bash(git check-ignore:*)",
      "Bash(git restore --source=:*)",
      "Bash(git clean -fd -- src)"
    ],
    "deny": [
      "Bash(git reset:*)",
      "Bash(git stash:*)",
      "Bash(git push:*)",
      "Bash(git checkout -- .:*)",
      "Bash(git commit -a:*)",
      "Bash(git commit --all:*)",
      "Bash(git add -A)",
      "Bash(git add .)",
      "Bash(git clean -fdx:*)",
      "Bash(npm install:*)",
      "Bash(npm i:*)",
      "Read(./.env)",
      "Read(./.env.*)"
    ]
  }
}
```

### 19.4 Project skills — `.claude/skills/<name>/SKILL.md`

| Skill | Triggers on | What it automates |
|---|---|---|
| `verificar-cambio` | "verificar", "correr el gate", "¿está todo en verde?", antes de marcar `done` | Ejecuta el arreglo `verify` de E3-T5 (= §20.1) línea por línea |

````markdown
---
name: verificar-cambio
description: Corre el gate completo de las correcciones de DogTor Angular (build sin avisos de presupuesto, todos los gates estáticos de los pasos 1-9 y lo que no debe cambiar frente a dogtor-base). Úsalo antes de marcar una tarea como done, después de un rollback, o cuando pidan "verificar", "correr el gate" o "¿está todo en verde?".
---

# Verificar cambio

## Cuándo usarlo

- Antes de marcar como `done` cualquier tarea de `blueprints/correcciones-profe/tasks.json`.
- Después de un rollback (`git restore --source=dogtor-sNN ... -- src`), para confirmar el estado.
- Cuando alguien pregunte si las correcciones del profesor siguen en verde.

## Pasos

1. Ubícate en `dogtor-angular/` (la carpeta que contiene `package.json`).
2. Lee `blueprints/correcciones-profe/tasks.json` y toma el arreglo `verify` de la tarea `E3-T5`:
   es exactamente el gate global de §20.1 del blueprint (build + los verify de E1-T1…E3-T4 +
   comprobaciones finales).
3. Ejecuta cada comando del arreglo, en orden, uno por uno. Cada uno debe terminar con exit 0.
   Antes de que exista la etiqueta `dogtor-s09`, la última línea (que comprueba las etiquetas)
   falla por diseño: en ese caso corre solo los `verify` de las tareas ya marcadas `done` más la
   tarea actual.
4. Si un comando falla, anota cuál y qué imprimió; vuelve a la tarea dueña de ese comando y
   corrige el código allí.

## Verify

```bash
npx ng build   # expect: exit 0
test -d src/app && test -z "$(grep -rn duenoId src/app)"   # expect: exit 0 (desde el paso 2)
test -d src/app && test -z "$(grep -rni whatsapp src/app)"   # expect: exit 0 (desde el paso 6)
```

## No hacer

- No editar ni saltarse un comando `verify` para que pase.
- No "arreglar" con `git reset --hard`, `git stash` ni `git add -A` sin pathspec: el repo padre
  tiene cambios sin commit de `Proyecto-Veterinaria-2.0/`.
- No instalar paquetes ni agregar tests/linters para verificar.
````

### 19.5 `.claude/rules/*.md`

| File | `paths` globs | Covers |
|---|---|---|
| `.claude/rules/angular-componentes.md` | `src/app/**` | Forma de los componentes, `input()`/`output()`, presentacionales sin servicios, `campoId`/`ancla`, NG0100, `d-block`, relación como objeto, git con pathspec |

```markdown
---
paths:
  - "src/app/**"
---

# Convenciones de componentes Angular (DogTor)

- Componente standalone: `<nombre>.component.ts` + `.html` + `.scss`, selector `app-<nombre>`,
  clase `<Nombre>Component`, `templateUrl`, `styleUrl` e `imports: [...]` explícito (vacío si no usa nada).
- Modelo a copiar: `src/app/pages/mascota-table-page/components/page-title/` y `.../mascota-table/`.
- Entradas: `nombre = input<T>(valorPorDefecto)` o `nombre = input.required<T>()`. Salidas:
  `nombre = output<T>()` y `this.nombre.emit(valor)`. En el template se leen como `nombre()`.
- Nunca nombrar una entrada `id`: Angular escribiría el mismo id en el host y en el elemento
  interno. Usar `campoId` (campos) o `ancla` (secciones).
- Componentes de `pages/<p>/components/` y de `components/` son presentacionales: no inyectan
  servicios. Solo la página (`pages/<p>/<p>.component.ts`) inyecta servicios, con `//DI` encima.
- Control de flujo: `@if (...) { } @else { }`, `@for (x of lista; track x.id) { } @empty { }`.
  Prohibido `*ngIf`, `*ngFor`, `NgIf`, `NgFor`.
- Imports relativos sin extensión ni alias (`'../../models/mascota.model'`). No hay alias `@/`.
- Nunca llamar desde el template a un método que construya un arreglo u objeto nuevo en cada
  llamada; precalcularlo en `ngOnInit` o en un campo `readonly`. Devolver un `string` es seguro.
- Mensajes de validación con `app-campo-error`: siempre `<div class="invalid-feedback d-block">`
  (el `~` de Bootstrap no cruza el host del componente).
- `.scss` de componente mínimo: primero utilidades de Bootstrap; si no tiene estilos propios, una
  sola línea de comentario `/* Sin estilos propios: ... */`.
- `Mascota.dueno` es un objeto `Dueno`. Prohibido volver a `duenoId` o llamar `getDuenoById`
  fuera de `src/app/service/`.
- Git: solo con pathspec (`-- src blueprints`). Prohibidos `git reset --hard`, `git stash`,
  `git add -A` sin pathspec y `git commit -a` (el repo padre tiene trabajo ajeno sin commit).
```

### 19.6 Verify-critical config and local infrastructure

**No se emite ningún archivo de configuración nuevo.** Los gates de §9 usan solo `npx ng build` y
coreutils (`test`, `grep`, `wc`, `cut`, `find`) más `git`. La configuración que `npx ng build`
necesita (`angular.json`, `tsconfig.json`, `tsconfig.app.json`) ya existe en el repo, está congelada
y la comprueba §20.1. No hay test runner, e2e runner, archivo de setup, alias de rutas ni servicio
local que provisionar, y ningún `Verify` recibe como argumento un archivo emitido por el blueprint:
todos los paths que nombran los gates los crea el paso dueño (lista **Archivos**) o ya existían.

| File | Path in the project | Which `Verify` commands need it | Resolution/env handling it carries | Bundle-path exclusion |
|---|---|---|---|---|
| `angular.json` (existente, congelado) | `angular.json` | todos (`npx ng build`) | none needed: every mandated package resolves plainly and this tool reads no env var | n/a — el builder compila solo desde `src/main.ts` (`"browser"`) y copia assets solo de `public/`; nunca recorre `blueprints/` |
| `tsconfig.app.json` (existente, congelado) | `tsconfig.app.json` | todos (`npx ng build`) | none needed: `"moduleResolution": "bundler"` resuelve los imports relativos sin extensión | n/a — `"files": ["src/main.ts"]` + `"include": ["src/**/*.d.ts"]`: nunca incluye `blueprints/` (y el bundle no contiene `.ts`) |
| `.claude/settings.json` (emitido) | `.claude/settings.json` | ninguno (permisos del agente) | none needed: no es configuración de una herramienta de build | n/a — Claude Code lee solo `.claude/settings.json` de la raíz; la copia dentro de `blueprints/…/workspace/` no se carga como configuración |

Los gates estáticos (`grep -r`, `find`) recorren solo `src` o `src/app`, nunca la raíz, así que el
bundle tampoco entra en ellos.

#### Resolution convention matrix

**The convention, stated once:** imports relativos entre módulos de la app, sin extensión y sin
alias (`import { Dueno } from '../../models/dueno.model';`). No existe alias `@/`.

| Context | Command that exercises it | Convention as it appears there | Config + literal setting that makes it work |
|---|---|---|---|
| Application source | `npx ng build` | `'../../models/dueno.model'` | `tsconfig.json` — `"moduleResolution": "bundler"` (existente) |
| Test files | NOT APPLICABLE — no hay archivos de test ni runner (§13) | — | — |
| Standalone scripts | NOT APPLICABLE — el proyecto no tiene scripts que importen `src/` | — | — |
| Build / bundle | `npx ng build` (esbuild dentro del builder `application`) | la misma forma relativa | `angular.json` — `"tsConfig": "tsconfig.app.json"` (existente) |
| Servidor de desarrollo | `npx ng serve` | la misma forma relativa | `angular.json` — `serve.buildTarget` usa la misma configuración de build |

#### Cross-artifact value reconciliation

| Shared value | Single source — the file that decides it | Literal value | Every other place it appears | Compared |
|---|---|---|---|---|
| Ruta del bundle | §19 de este blueprint | `blueprints/correcciones-profe/` | §10 Bootstrap (copia de `workspace/`) · `workspace/CLAUDE.md` · `workspace/AGENTS.md` · `workspace/.claude/settings.json` (`cp -R blueprints/correcciones-profe/workspace/. .`) · SKILL.md · `taskFiles` de E3-T5 | yes |
| Salida del build | `angular.json` — `"outputPath": "dist/dogtor-angular"` (+ `browser/` del builder `application`) | `dist/dogtor-angular/browser/index.html` | §3 · §12 · paso 10 / §20.1 | yes |
| Comando de build | `package.json` `"build": "ng build"` / CLI | `npx ng build` | todos los `Verify` · §10 · `workspace/CLAUDE.md` · `workspace/AGENTS.md` · `settings.json` (`Bash(npx ng build:*)`) · SKILL.md | yes |
| Puerto del servidor de desarrollo | por defecto de `ng serve` | `4200` | `workspace/CLAUDE.md` · `workspace/AGENTS.md` · §12 · checklist de §20.1 | yes |
| Rama de trabajo | §10 Bootstrap | `feature/angular-correcciones` | `workspace/CLAUDE.md` · §12 | yes |
| Etiqueta base | §10 Bootstrap | `dogtor-base` | paso 1 (Verify y Rollback) · `checkpoint`/rollback de E1-T1 · §12 · §20.1 · `workspace/CLAUDE.md` | yes |
| Etiquetas de paso | campo `checkpoint` de `tasks.json` | `dogtor-s01` … `dogtor-s10` | Checkpoint y Rollback de cada paso de §9 y de cada epic · §20.1 · `workspace/CLAUDE.md` | yes |
| Selectores de componentes | `selector` de cada `.component.ts` | `app-estado-badge`, `app-mascota-avatar`, `app-campo-error`, `app-campo-texto`, `app-dueno-select`, `app-foto-preview`, `app-dueno-card`, `app-mascota-info-card`, `app-registro-medico-item`, `app-hero-carousel`, `app-section-header`, `app-service-card`, `app-plan-card`, `app-team-card`, `app-testimonio-card`, `app-cta-banner`, `app-contacto-form` | plantillas que los usan · gates `grep -qF '<app-…'` · §5 · §6 | yes |
| Ancla de contacto | `landing.component.html` — `ancla="contacto"` | `contacto` | `landing.component.ts` (`getElementById('contacto')`) · gate del paso 9 | yes |

La contradicción entre dos de estos valores la detectaría `npx ng build` en el mismo paso (un
selector mal escrito es un error de compilación con `strictTemplates`) o el gate del paso que
escribe ambos lados (paso 9 para el ancla; paso 10 para la salida del build).

#### Byte-exact artifact reconciliation

NOT APPLICABLE — this blueprint authors no byte-exact expected output. Ningún gate compara la salida
de un programa contra un archivo dorado: los `grep -qF` buscan textos que el mismo paso escribe en su
propio código fuente (por ejemplo "¡Gracias! Te contactaremos pronto." dentro de
`contacto-form.component.html`), no mensajes producidos por el runtime.

---

## 20. Gate de aceptación, riesgos y decisiones (Acceptance Gate, Risks & Decision Log)

### 20.1 Global acceptance gate

El cambio está **terminado** cuando cada comando de abajo termina con exit 0 desde `dogtor-angular/`,
después del Checkpoint del paso 9. Es exactamente el arreglo `verify` de la tarea E3-T5 (paso 10):
build sin avisos de presupuesto, todos los gates de los pasos 1–9, salida del build donde
`angular.json` dice, interfaces congeladas iguales a `dogtor-base`, ningún `*.spec.ts` y las
etiquetas de los pasos presentes.

```bash
npx ng build > /tmp/dogtor-build.log 2>&1 && test -z "$(grep -i budget /tmp/dogtor-build.log)"  # expect: exit 0 y ningún aviso de presupuesto (anyComponentStyle 4kB/8kB)
test -f src/app/models/mascota.model.ts && grep -qF 'dueno?: Dueno;' src/app/models/mascota.model.ts && grep -qF "import { Dueno } from './dueno.model';" src/app/models/mascota.model.ts  # expect: exit 0
test "$(grep -n 'private duenoService = inject(DuenoService);' src/app/service/mascota.service.ts | cut -d: -f1)" -lt "$(grep -n 'private mascotaArray: Mascota\[\] = \[' src/app/service/mascota.service.ts | cut -d: -f1)"  # expect: exit 0 (duenoService se declara antes del arreglo)
a="$(git show dogtor-base:./src/app/service/mascota.service.ts | grep -oE 'duenoId: [0-9]+ \}' | grep -oE '[0-9]+')"; b="$(grep -oE 'getDuenoById\([0-9]+\) \}' src/app/service/mascota.service.ts | grep -oE '[0-9]+')"; test -n "$a" && test "$a" = "$b"  # expect: exit 0 (cada fila usa el mismo N que tenía en dogtor-base)
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts && test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && test -f src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -nE 'DuenoService|getDuenoById|getNombreDueno' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0, sin coincidencias
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF "{{ mascota.dueno?.nombre ?? '—' }}" src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rn duenoId src/app)"  # expect: exit 0, cero ocurrencias
test -d src/app && test -z "$(grep -rn getDuenoById src/app --include=*.ts | grep -v '^src/app/service/')"  # expect: exit 0, solo se usa dentro de src/app/service/
test -f src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: new FormControl<Dueno | null>(null, [Validators.required]),' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: mascota.dueno ?? null,' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: formValue.dueno ?? undefined,' src/app/pages/mascota-form/mascota-form.component.ts  # expect: exit 0
test -d src/app/pages/mascota-form && grep -rqF 'compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);' src/app/pages/mascota-form && grep -rqF '[compareWith]="compararDuenos"' src/app/pages/mascota-form && grep -rqF '<option [ngValue]="dueno">' src/app/pages/mascota-form  # expect: exit 0
test -f src/app/service/mascota.service.ts && grep -qF 'm.dueno?.id === dueno.id' src/app/service/mascota.service.ts  # expect: exit 0
(for c in estado-badge mascota-avatar; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 6 archivos existen
grep -qF 'activa = input.required<boolean>();' src/app/components/estado-badge/estado-badge.component.ts && grep -qF 'nombre = input.required<string>();' src/app/components/mascota-avatar/mascota-avatar.component.ts  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '<app-mascota-avatar' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '[tamano]="56"' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app/pages/mascota-detail && grep -rqF '[tamano]="180"' src/app/pages/mascota-detail && grep -rqF '<app-estado-badge [activa]=' src/app/pages/mascota-detail  # expect: exit 0
test -d src/app && test -z "$(grep -rln fotoPorDefecto src/app --include=*.ts | grep -v '^src/app/components/mascota-avatar/')"  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss && test -f src/app/pages/mascota-detail/mascota-detail.component.scss && test -z "$(grep -n '^\.foto' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss src/app/pages/mascota-detail/mascota-detail.component.scss)"  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.html && test "$(wc -l < src/app/pages/mascota-form/mascota-form.component.html)" -le 70  # expect: exit 0 (≤ 70 líneas)
(for c in campo-error campo-texto; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done; for c in dueno-select foto-preview; do for e in ts html scss; do test -f src/app/pages/mascota-form/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 12 archivos existen
grep -qF '<div class="invalid-feedback d-block">' src/app/components/campo-error/campo-error.component.html  # expect: exit 0
f=src/app/pages/mascota-form/mascota-form.component.html; test -f $f && grep -qF 'campoId="nombre"' $f && grep -qF 'campoId="raza"' $f && grep -qF 'campoId="edad"' $f && grep -qF 'campoId="fotoUrl"' $f && grep -qF 'campoId="vacunas"' $f && grep -qF '<app-foto-preview' $f && grep -qF '<app-dueno-select' $f  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.ts && test -f src/app/pages/mascota-form/mascota-form.component.html && test -z "$(grep -nE 'campoInvalido|invalid-feedback' src/app/pages/mascota-form/mascota-form.component.ts src/app/pages/mascota-form/mascota-form.component.html)"  # expect: exit 0
grep -qF '.preview {' src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss && test -f src/app/pages/mascota-form/mascota-form.component.scss && test -z "$(grep -n 'preview' src/app/pages/mascota-form/mascota-form.component.scss)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.html && test "$(wc -l < src/app/pages/mascota-detail/mascota-detail.component.html)" -le 45  # expect: exit 0 (≤ 45 líneas)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-mascota-info-card' src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-registro-medico-item' src/app/pages/mascota-detail/mascota-detail.component.html && test -z "$(grep -n 'getNombre' src/app/pages/mascota-detail/mascota-detail.component.html)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.ts && grep -qF 'registros: RegistroVista[] = [];' src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -n 'DuenoService' src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0
f=src/app/components/dueno-card/dueno-card.component; test -f $f.html && grep -qF 'Sin dueño asignado' $f.html && grep -qF 'dueno = input<Dueno | undefined>();' $f.ts  # expect: exit 0
f=src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts; test -f $f && grep -qF 'registro = input.required<RegistroMedico>();' $f && grep -qF 'veterinario = input.required<string>();' $f && grep -qF 'drogas = input<string[]>([]);' $f && test -z "$(grep -n 'inject(' $f)"  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF "providedIn: 'root'" $f && grep -qF 'getSlides()' $f && grep -qF 'getServicios()' $f && grep -qF 'getPlanes()' $f && grep -qF 'getEquipo()' $f && grep -qF 'getTestimonios()' $f && grep -qF 'getContacto()' $f  # expect: exit 0
f=src/app/service/landing.service.ts; grep -qF 'photo-1552053831-71594a27632d?w=1200' $f && grep -qF 'photo-1537151608804-ea6f117f73d2?w=1200' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.ts && test -z "$(grep -n unsplash src/app/pages/landing/landing.component.ts)"  # expect: exit 0
grep -qF 'slides = input.required<Slide[]>();' src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts && grep -qF '<app-hero-carousel [slides]="slides">' src/app/pages/landing/landing.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rni whatsapp src/app)"  # expect: exit 0, cero coincidencias
f=src/app/pages/landing/components/plan-card/plan-card.component.ts; test -f $f && grep -qF 'plan = input.required<Plan>();' $f && grep -qF 'planElegido = output<Plan>();' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-section-header' $f && grep -qF '<app-service-card' $f && grep -qF '(planElegido)="elegirPlan($event)"' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.ts; test -f $f && grep -qF "document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });" $f && grep -qF 'planSeleccionado: Plan | undefined;' $f  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && (for i in bi-heart-pulse bi-shield-check bi-scissors bi-capsule bi-house-heart bi-droplet; do grep -qF "'$i'" $f || exit 1; done)  # expect: exit 0, los 6 iconos
f=src/app/service/landing.service.ts; grep -qF "precio: '\$49.900'" $f && grep -qF "precio: '\$89.900'" $f && grep -qF "precio: '\$149.900'" $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-team-card' $f && grep -qF '<app-testimonio-card' $f && grep -qF '<app-cta-banner' $f && grep -qF 'ruta="/mascotas"' $f  # expect: exit 0
grep -qF 'bi-person-circle' src/app/pages/landing/components/team-card/team-card.component.html && grep -qF 'bi-star-fill' src/app/pages/landing/components/testimonio-card/testimonio-card.component.html  # expect: exit 0
grep -qF 'ruta = input.required<string>();' src/app/pages/landing/components/cta-banner/cta-banner.component.ts && grep -qF '[routerLink]="ruta()"' src/app/pages/landing/components/cta-banner/cta-banner.component.html  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF 'Dr. Andres Felipe' $f && grep -qF 'Dra. Laura Jimenez' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.html && test "$(wc -l < src/app/pages/landing/landing.component.html)" -le 60  # expect: exit 0 (≤ 60 líneas)
f=src/app/pages/landing/landing.component.html; test -f $f && (for t in app-hero-carousel app-service-card app-plan-card app-team-card app-testimonio-card app-cta-banner app-contacto-form; do grep -qF "<$t" $f || exit 1; done) && grep -qF 'ancla="contacto"' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.ts; test -f $f && grep -qF 'enviado = output<{ nombre: string; correo: string; mensaje: string }>();' $f && grep -qF 'effect(' $f && grep -qF 'Me interesa el plan ${plan.nombre}.' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.html; test -f $f && grep -qF '¡Gracias! Te contactaremos pronto.' $f && grep -qF '<app-campo-texto' $f && grep -qF '<app-campo-error' $f  # expect: exit 0
test -d src/app && test -z "$(grep -rnE 'HttpClient|fetch\(' src/app)"  # expect: exit 0, nada se envía
test -f dist/dogtor-angular/browser/index.html && grep -qF '<app-root>' dist/dogtor-angular/browser/index.html  # expect: exit 0, el build quedó en el outputPath de angular.json
git diff --quiet dogtor-base -- src/app/app.routes.ts src/styles.scss src/app/components/navbar src/app/components/footer angular.json package.json package-lock.json  # expect: exit 0, sin cambios frente a dogtor-base
test -d src && test -z "$(find src -name '*.spec.ts')"  # expect: exit 0, no se agregó ningún *.spec.ts
git rev-parse -q --verify refs/tags/dogtor-base >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null && git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null  # expect: exit 0, existen dogtor-base y dogtor-s01…dogtor-s09
```

Cada expectativa es una propiedad, no un conteo derivado; los únicos números son límites del
pedido (70/45/60 líneas). Ninguna línea pasa con un exit distinto de 0 y ninguna depende de su propio
commit.

Plus these manual gates, each checked once before delivery:

- [ ] Cada paso tiene su etiqueta: `git tag -l 'dogtor-s*'` lista `dogtor-s01` … `dogtor-s10`
      (más `dogtor-base`). El repositorio es el repo padre existente; el Bootstrap de §10 crea la
      etiqueta base.
- [ ] Los archivos de §10 *Files that must be committed* están en git, una ruta por invocación:
      `git ls-files --error-unmatch CLAUDE.md`, `git ls-files --error-unmatch AGENTS.md`,
      `git ls-files --error-unmatch .claude/settings.json`,
      `git ls-files --error-unmatch package-lock.json`,
      `git ls-files --error-unmatch blueprints/correcciones-profe/tasks.json` — cada una exit 0. Y
      ninguna está ignorada: `git check-ignore -q CLAUDE.md; test $? -eq 1` (y la misma línea para
      cada ruta; 1 = no ignorada, 128 = error de uso, que ahora falla).
- [ ] El `.gitignore` existía antes del commit base: `git log --diff-filter=A --format=%H -- .gitignore`
      muestra el commit de `dogtor-base` (no uno de un paso de §9).
- [ ] Byte-exact reconciliation: NOT APPLICABLE (§19.6 no tiene artefactos byte-exactos).
- [ ] El Bootstrap de §10 se volvió a ejecutar una vez sobre el árbol ya preparado, **terminó con
      exit 0** en cada línea y no cambió nada que importe: `git diff --quiet dogtor-base -- package.json package-lock.json`
      sigue en exit 0 y `npx ng build` sigue encontrando sus binarios.
- [ ] Cada fila de §19.6 *Cross-artifact value reconciliation* dice `Compared: yes`, y el gate de
      arriba corrió desde `dogtor-angular/` con el bundle presente.
- [ ] El trabajo ajeno del repo padre sigue intacto: desde la raíz del repo padre,
      `git status --short -- Proyecto-Veterinaria-2.0` muestra los mismos archivos modificados que
      antes del Bootstrap y `git log --format=%H dogtor-base..HEAD -- Proyecto-Veterinaria-2.0`
      no imprime nada.
- [ ] §9.1: NOT APPLICABLE (no es una migración).
- [ ] Cada No-objetivo de §1 sigue sin construir.
- [ ] Variables de entorno: NOT APPLICABLE (el proyecto no usa ninguna).
- [ ] Recorrido manual con teclado y con zoom al 200 % de la landing y del formulario de mascota (§15).
- [ ] Un rollback de prueba: `git restore --source=dogtor-s09 --staged --worktree -- src && git clean -fd -- src`
      no cambia nada (el paso 10 no toca `src/`), y `git status --short -- src` no imprime nada.

**Checklist visual manual (no es un gate; se hace después del paso 10 con `npx ng serve` y el
navegador integrado o cualquier navegador en http://localhost:4200):**

- [ ] `/mascotas`: la columna Dueño muestra nombres (por ejemplo "Juan Pérez" en Max) y las fotos son círculos de 56 px.
- [ ] `/mascota/update/3` (Rocky): el selector de dueño muestra "María López" ya seleccionada; borrar el nombre y salir del campo muestra "El nombre es obligatorio." en rojo.
- [ ] Guardar una mascota editada conserva su dueño en la tabla y en `/mascota/<id>`.
- [ ] `/mascota/1`: tarjeta con foto de 180 px, badge "Activa", tarjeta de dueño con teléfono y dirección, y dos registros médicos con veterinario y drogas.
- [ ] `/`: carrusel funcionando (flechas e indicadores), 6 servicios, 3 planes ("Plus" destacado), 3 miembros del equipo, 3 testimonios con estrellas, banner que lleva a `/mascotas` y sección de contacto; no aparece el botón de WhatsApp.
- [ ] Elegir el plan "Plus" baja a Contáctanos y el mensaje dice "Me interesa el plan Plus."; enviar vacío muestra errores; enviar válido muestra "¡Gracias! Te contactaremos pronto." y limpia el formulario.
- [ ] La consola del navegador no muestra errores (en particular ningún NG0100).

**No warnings are ignored.** El único aviso tolerado es el preexistente de Bootstrap ("4 rules
skipped due to selector errors"), que no lo genera este cambio; cualquier otro aviso de `ng build`
(en especial uno de presupuesto) hace fallar la primera línea del gate.

### 20.2 Risk register

| Risk | Likelihood | Impact | Early signal | Mitigation |
|---|---|---|---|---|
| NG0100 (ExpressionChangedAfterItHasBeenChecked) por arreglos calculados en el template | M | M | Error rojo en la consola con `ng serve` en el detalle | Paso 5: el padre arma `registros` una vez en `ngOnInit`; los hijos solo pintan. Regla en `.claude/rules/angular-componentes.md` |
| El mensaje de error no se ve dentro de un componente hijo (Bootstrap usa `.is-invalid ~ .invalid-feedback`) | H si se omite `d-block` | M | Campo en rojo sin texto | `campo-error` siempre renderiza `invalid-feedback d-block`; gate del paso 4 lo exige |
| Al editar, el `<select>` de dueño queda vacío porque compara objetos por referencia | M | H | `/mascota/update/3` sin dueño seleccionado | Semilla con la misma instancia de `DuenoService` (paso 1) + `compareWith` por id (pasos 2 y 4) |
| Error de presupuesto `anyComponentStyle` (8 kB) al crecer un `.scss` | L | M | `npx ng build` falla o avisa "budget" | SCSS mínimo y utilidades de Bootstrap; el SCSS más grande (carrusel) pesa ~0.7 kB; §20.1 falla ante cualquier aviso de presupuesto |
| Daño al trabajo sin commit de `Proyecto-Veterinaria-2.0/` en el repo padre | M si se usa un comando global | H | `git status` del repo padre cambia | Git solo con pathspec (`-- src blueprints`), rollback con `git restore … -- src`; `settings.json` niega `git reset`, `git stash`, `git commit -a` y `git add` sin pathspec |
| URLs externas de imágenes (Unsplash, icon-icons) que dejan de responder | L | L | Imagen rota en la demo | Solo se conservan las URLs que ya existían; equipo y testimonios usan iconos de bootstrap-icons |
| Estado intermedio del paso 1: una mascota guardada desde el formulario queda sin `dueno` | H (solo entre pasos 1 y 2) | L | La tabla muestra "—" para esa mascota | El paso 2 lo cierra en la sesión siguiente; F5 vuelve a la semilla |
| Alcance: tentación de convertir también `RegistroMedico` a objetos | M | M | Un paso toca `registro-medico.service.ts` | Es No-objetivo; ningún paso lista ese archivo; §20.4 lo deja como siguiente cambio |

### 20.3 Decision log

| # | Decision | Rejected alternative | Why | Would reverse if |
|---|---|---|---|---|
| 1 | Sin tests: los gates son `npx ng build` + comprobaciones estáticas | Agregar Karma/Jasmine o Vitest y escribir specs | Convención del repo (`skipTests: true`, cero specs); agregar runner es un No-objetivo y ampliaría la entrega | El curso exija pruebas unitarias |
| 2 | Relación como objeto solo para `Mascota → Dueno` | Convertir también `RegistroMedico`, `Dueno.usuarioId`, `Administrador.usuarioId` y `Veterinario.usuarioId` | Decisión del usuario para acotar la entrega | El profesor lo pida para las demás entidades |
| 3 | La semilla de `MascotaService` resuelve `getDuenoById(N)` dentro del servicio | Escribir los objetos `Dueno` literales dentro de cada mascota | La queja era que los *componentes* resolvían ids; así el objeto es la misma instancia de `DuenoService` (necesario para `compareWith` y para que un cambio de dueño se vea en todas partes) | Haya un backend que entregue la mascota con su dueño embebido |
| 4 | Cambio en dos pasos con coexistencia (`duenoId` + `dueno` en el paso 1) | Un solo paso que cambie modelo, servicio, tabla, detalle y formulario | Cada paso deja el build en verde y queda en ≤ 5 archivos | — (es la regla de brownfield: nunca un cambio de golpe) |
| 5 | `getMascotasByDueno(dueno: Dueno)` | Mantener `getMascotasByDueno(duenoId: number)` | El gate exige cero `duenoId` en `src/app` y el método no tiene llamadores; recibir el objeto es coherente con la corrección | Un llamador futuro solo tenga el id |
| 6 | En el paso 1 el detalle usa un getter `dueno` y no toca su HTML | Cambiar también el HTML del detalle en el paso 1 | Mantiene el paso 1 en 5 archivos; el HTML del detalle se rehace en el paso 5 de todas formas | — |
| 7 | Entradas `campoId` y `ancla` en vez de `id` | Una entrada llamada `id` | Con atributo estático, Angular escribe el `id` en el host y en el elemento interno: ids duplicados y `<label for>` apuntando al host | Angular deje de reflejar atributos estáticos en el host |
| 8 | `effect()` en `contacto-form` para precargar el mensaje con el plan | `ngOnChanges` | `plan` es una entrada de señal; `effect()` es la forma idiomática y corre antes de pintar la vista del componente | Aparezca un NG0100 o un bucle de efectos en `ng serve` |
| 9 | Precios como texto ya formateado (`'$49.900'`) | `CurrencyPipe` con `registerLocaleData('es-CO')` | Tres precios fijos no justifican configurar un locale | Los precios vengan de datos o se calculen |
| 10 | Commit y rollback con pathspec y etiquetas `dogtor-*` | `git reset --hard step-NN` como dice la plantilla | El repo padre tiene trabajo ajeno sin commit que `reset --hard` destruiría | `dogtor-angular/` pase a ser un repo propio y limpio |
| 11 | 3 epics (pasos 1–2, 3–5, 6–10) | 2 epics de 5 pasos, como pide la regla de conteo para 10 pasos | El usuario aprobó un epic por corrección del profesor; agrupar no cambia ni el orden ni los gates | Se use `/architect-next` con herramientas que rechacen epics de menos de 5 tareas |
| 12 | Copia de `workspace/` con sobrescritura (`cp -R`, sin `-n`) | Copia sin pisar (`cp -Rn` o `rsync --ignore-existing`) | `workspace/` no contiene archivos que un paso edite (ni manifiesto ni lockfile), así que pisarlos es idempotente; `cp -Rn` sale con 1 en BSD al saltar archivos | Un paso empiece a editar `CLAUDE.md` o `.claude/**` |
| 13 | El botón de WhatsApp se elimina | Moverlo a la landing | Decisión del usuario | El usuario lo vuelva a pedir |

### 20.4 What to build next

1. Relaciones como objeto en `RegistroMedico` (`mascota`, `veterinario`, `drogas`) — cuando el profesor lo pida para las demás entidades.
2. `Dueno.usuario`, `Administrador.usuario` y `Veterinario.usuario` como objetos — mismo disparador.
3. Pruebas unitarias de los componentes nuevos (`campo-error`, `plan-card`, `contacto-form`) — cuando el curso exija tests.
4. Ajustar los pares de color que no pasan contraste y respetar `prefers-reduced-motion` — cuando se permita tocar `src/styles.scss`.
5. Envío real del formulario de contacto — cuando exista un endpoint en el backend Spring.

---

*End of blueprint. Build order is §9. Stop when §20.1 is green.*
