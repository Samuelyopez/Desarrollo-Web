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
