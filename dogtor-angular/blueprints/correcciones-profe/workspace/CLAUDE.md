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
