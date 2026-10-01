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
