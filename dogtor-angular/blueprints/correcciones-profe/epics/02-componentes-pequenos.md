# Epic 02: Componentes pequeños con input()/output()

> Al terminar este epic, la tabla, el formulario y el detalle de mascota se arman con componentes pequeños y presentacionales; el HTML del formulario tiene ≤ 70 líneas y el del detalle ≤ 45.

| | |
|---|---|
| **Epic id** | `02-componentes-pequenos` |
| **Tasks** | `E2-T1` … `E2-T3` (pasos 3–5 de `blueprint.md` §9) |
| **Depends on** | `01-mascota-dueno-objeto` |
| **Unlocks** | `03-landing-completa` |
| **Parallel with** | ninguno (`03` reutiliza `campo-texto` y `campo-error`, que crea este epic) |

No necesitas ningún otro archivo para completar este epic. Todo lo de abajo se repite aquí a propósito.

---

## Stack

Angular 19.2 standalone · TypeScript 5.7 strict + `strictTemplates` · Bootstrap 5.3 + bootstrap-icons ·
SCSS por componente · datos en arreglos de servicios `providedIn: 'root'` (sin backend, sin base de datos) ·
sin autenticación · sin despliegue (solo `npx ng serve`).
Gestor de paquetes: `npm`. Node v24.14.1 en la máquina. Las versiones están en `package-lock.json`:
léelo, nunca adivines una. **No se instala ni actualiza nada.**

| Task | Command |
|---|---|
| Instalar (ya lo hizo el Bootstrap) | `npm ci` |
| Build (gate) | `npx ng build` |
| Servidor de desarrollo | `npx ng serve` — http://localhost:4200 |
| Gate completo | skill `verificar-cambio` |

**Gate:** `npx ng build` más todos los comandos `verify` de la tarea, con exit 0, antes de marcar una tarea `done`. No hay linter, formatter ni test runner y no se agregan. Ningún comando necesita servicios locales.

Todos los comandos se ejecutan desde `dogtor-angular/` (la carpeta con `package.json`), en Git Bash.

## Directory subtree

Solo lo que toca este epic:

```
src/app/
  components/                            # compartidos (aquí ya viven navbar y footer)
    estado-badge/                        # NUEVO en E2-T1
    mascota-avatar/                      # NUEVO en E2-T1
    campo-error/                         # NUEVO en E2-T2
    campo-texto/                         # NUEVO en E2-T2
    dueno-card/                          # NUEVO en E2-T3
  pages/
    mascota-table-page/components/mascota-table/
      mascota-table.component.ts|html|scss   # existe; E2-T1 usa app-mascota-avatar
    mascota-form/
      mascota-form.component.ts|html|scss    # existe; E2-T2 lo parte
      components/dueno-select/           # NUEVO en E2-T2
      components/foto-preview/           # NUEVO en E2-T2
    mascota-detail/
      mascota-detail.component.ts|html|scss  # existe; E2-T1 y E2-T3 lo parten
      components/mascota-info-card/      # NUEVO en E2-T3
      components/registro-medico-item/   # NUEVO en E2-T3
  models/                                # solo lectura: mascota, dueno, registro-medico
  service/                               # solo lectura: mascota, dueno, registro-medico, droga, usuario
```

Todo lo demás está fuera de alcance. Si una tarea parece exigir editar un archivo que no está aquí, detente y repórtalo.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| `Mascota` | lee todos; `dueno` es objeto (epic 01) | sin cambios de forma |
| `Dueno` | lee `id`, `nombre`, `telefono`, `direccion` | sin cambios |
| `RegistroMedico` | lee `fecha`, `diagnostico`, `tratamiento`, `veterinarioId`, `drogaIds` | sin cambios (sus ids siguen siendo ids: No-objetivo) |

## Contracts

**Consumed** — ya existe, no lo reconstruyas:

| From | Interface | Guarantee |
|---|---|---|
| `01-mascota-dueno-objeto` | `Mascota.dueno?: Dueno` | objeto, misma instancia que `DuenoService` |
| `01-mascota-dueno-objeto` | `mascotaForm.controls.dueno: FormControl<Dueno \| null>` | requerido; se precarga con `mascota.dueno ?? null` |
| repo existente | `UsuarioService.getUsuarioById`, `DrogaService.getDrogaById`, `RegistroMedicoService.getRegistrosByMascota` | sin cambios |

**Produced** — los epics siguientes dependen de estas firmas:

| Export | Signature | Used by |
|---|---|---|
| `src/app/components/campo-error/campo-error.component.ts` → `CampoErrorComponent` | `control = input.required<AbstractControl>()`, `mensajes = input<Record<string, string>>({})` | `03-landing-completa` (E3-T4) |
| `src/app/components/campo-texto/campo-texto.component.ts` → `CampoTextoComponent` | `campoId`, `etiqueta` (`input.required<string>()`), `control = input.required<FormControl<string \| null>>()`, `placeholder = input<string>('')`, `mensajes` | `03-landing-completa` (E3-T4) |
| `src/app/components/mascota-avatar/…` → `MascotaAvatarComponent` | `fotoUrl`, `nombre` (requerida), `tamano = 56` | este epic |
| `src/app/components/estado-badge/…` → `EstadoBadgeComponent` | `activa = input.required<boolean>()` | este epic |
| `src/app/components/dueno-card/…` → `DuenoCardComponent` | `dueno = input<Dueno \| undefined>()` | este epic |

## Conventions that bite in this area

- Standalone, `templateUrl` + `styleUrl` (`.scss`), `imports: []` explícito, selector `app-<nombre>`, clase `<Nombre>Component`; patrón de `page-title`/`mascota-table`.
- Los subcomponentes son presentacionales: **no inyectan servicios**. Solo la página inyecta, con `//DI`.
- Nunca una entrada llamada `id` (usa `campoId`): con atributo estático Angular duplicaría el id en el host.
- `campo-error` siempre `<div class="invalid-feedback d-block">`: el `~` de Bootstrap no cruza el host.
- Nada de métodos de template que devuelvan arreglos nuevos (NG0100): se precalcula en `ngOnInit`.
- Git **solo con pathspec**: nunca `git reset --hard`, `git stash`, `git add -A` sin pathspec ni `git commit -a`.

Reglas completas: `CLAUDE.md`. Reglas del área: `.claude/rules/angular-componentes.md`. Ambos están en la raíz del proyecto desde el Bootstrap.

---

## Tasks

En el mismo orden que `tasks.json`. Ese orden es el orden de construcción.

### `E2-T1` — Componentes compartidos estado-badge y mascota-avatar

**Depends on:** `E1-T2` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 3

**Objetivo:** Después de este paso la foto de mascota y la etiqueta Activa/Inactiva son componentes compartidos con `input()`, usados por la tabla y el detalle.

Crea dos componentes compartidos en `src/app/components/` (donde ya viven navbar y footer), siguiendo el patrón de `page-title` y `mascota-table`: standalone, `templateUrl` + `styleUrl`, `imports: []` explícito y entradas con `input()` / `input.required()`.

`mascota-avatar` es ahora el único dueño de la constante `fotoPorDefecto` (antes estaba duplicada en tabla y detalle) y fija el tamaño con `[style.width.px]`/`[style.height.px]`, por eso desaparecen las reglas `.foto` de las hojas de estilo de la tabla y del detalle. La tabla usa `tamano` 56 y el detalle 180. `estado-badge` reemplaza el `@if/@else` de badges del detalle.

Cada componente nuevo son 3 archivos (`.ts`, `.html`, `.scss`) que se crean juntos; en `tasks.json` se listan como un glob por carpeta.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/components/estado-badge/*`
- `src/app/components/mascota-avatar/*`
- `src/app/pages/mascota-table-page/components/mascota-table/*`
- `src/app/pages/mascota-detail/mascota-detail.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se listan `src/app/components/estado-badge/` y `src/app/components/mascota-avatar/` **THE SYSTEM SHALL** contener cada uno sus archivos `.component.ts`, `.component.html` y `.component.scss`, con `activa = input.required<boolean>();` y `nombre = input.required<string>();` respectivamente.
3. **WHEN** se lee `mascota-table.component.html` **THE SYSTEM SHALL** usar `<app-mascota-avatar` con `[tamano]="56"`.
4. **WHEN** se busca en `src/app/pages/mascota-detail/` **THE SYSTEM SHALL** encontrar `<app-mascota-avatar` con `[tamano]="180"` y `<app-estado-badge [activa]=`.
5. **WHEN** se busca `fotoPorDefecto` en los `.ts` de `src/app` **THE SYSTEM SHALL** encontrarlo solo en `src/app/components/mascota-avatar/`.
6. **WHEN** se leen `mascota-table.component.scss` y `mascota-detail.component.scss` **THE SYSTEM SHALL** no contener ninguna regla que empiece por `.foto`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
(for c in estado-badge mascota-avatar; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 6 archivos existen
grep -qF 'activa = input.required<boolean>();' src/app/components/estado-badge/estado-badge.component.ts && grep -qF 'nombre = input.required<string>();' src/app/components/mascota-avatar/mascota-avatar.component.ts  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '<app-mascota-avatar' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF '[tamano]="56"' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
test -d src/app/pages/mascota-detail && grep -rqF '[tamano]="180"' src/app/pages/mascota-detail && grep -rqF '<app-estado-badge [activa]=' src/app/pages/mascota-detail  # expect: exit 0
test -d src/app && test -z "$(grep -rln fotoPorDefecto src/app --include=*.ts | grep -v '^src/app/components/mascota-avatar/')"  # expect: exit 0
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss && test -f src/app/pages/mascota-detail/mascota-detail.component.scss && test -z "$(grep -n '^\.foto' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.scss src/app/pages/mascota-detail/mascota-detail.component.scss)"  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T1: Componentes compartidos estado-badge y mascota-avatar" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null || git tag dogtor-s03
git rev-parse -q --verify refs/tags/dogtor-s03 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s02`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s02 --staged --worktree -- src && git clean -fd -- src
```

### `E2-T2` — Formulario de mascota dividido en componentes de campo

**Depends on:** `E2-T1` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 4

**Objetivo:** Después de este paso el formulario de mascota se arma con `app-campo-texto`, `app-dueno-select` y `app-foto-preview`, y su HTML tiene como máximo 70 líneas.

Dos componentes compartidos nuevos en `src/app/components/`: `campo-error` (pinta el mensaje del primer error presente cuando el control está tocado e inválido) y `campo-texto` (label + `<input [formControl]>` + `campo-error`). Dos componentes locales de la página en `src/app/pages/mascota-form/components/`: `dueno-select` (se lleva `compararDuenos` y el `<select>`) y `foto-preview` (la vista previa de 96 px y su regla `.preview`).

`campo-error` **debe** renderizar `<div class="invalid-feedback d-block">`: el selector de Bootstrap `.is-invalid ~ .invalid-feedback` es de hermanos y no atraviesa el elemento host del componente, así que sin `d-block` el mensaje nunca se ve.

Las entradas de id se llaman `campoId` (no `id`): un `input` llamado `id` con atributo estático haría que Angular escriba el mismo id en el host y en el `<input>`, y el `<label>` apuntaría al host. El padre pasa los controles tipados (`mascotaForm.controls.nombre`, etc.) y un objeto `mensajes` con el texto de cada validador. `campoInvalido()` y el marcado duplicado de `invalid-feedback` salen del padre.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/components/campo-error/*`
- `src/app/components/campo-texto/*`
- `src/app/pages/mascota-form/components/dueno-select/*`
- `src/app/pages/mascota-form/components/foto-preview/*`
- `src/app/pages/mascota-form/mascota-form.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se cuentan las líneas de `src/app/pages/mascota-form/mascota-form.component.html` **THE SYSTEM SHALL** reportar 70 o menos.
3. **WHEN** se lee `src/app/components/campo-error/campo-error.component.html` **THE SYSTEM SHALL** renderizar `<div class="invalid-feedback d-block">`.
4. **WHEN** se lee `mascota-form.component.html` **THE SYSTEM SHALL** contener `campoId="nombre"`, `campoId="raza"`, `campoId="edad"`, `campoId="fotoUrl"`, `campoId="vacunas"`, `<app-foto-preview` y `<app-dueno-select`.
5. **WHEN** se buscan `campoInvalido` e `invalid-feedback` en `mascota-form.component.ts` y `mascota-form.component.html` **THE SYSTEM SHALL** encontrar cero coincidencias.
6. **WHEN** se buscan reglas `.preview` **THE SYSTEM SHALL** encontrarla en `foto-preview.component.scss` y no en `mascota-form.component.scss`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/mascota-form/mascota-form.component.html && test "$(wc -l < src/app/pages/mascota-form/mascota-form.component.html)" -le 70  # expect: exit 0 (≤ 70 líneas)
(for c in campo-error campo-texto; do for e in ts html scss; do test -f src/app/components/$c/$c.component.$e || exit 1; done; done; for c in dueno-select foto-preview; do for e in ts html scss; do test -f src/app/pages/mascota-form/components/$c/$c.component.$e || exit 1; done; done)  # expect: exit 0, los 12 archivos existen
grep -qF '<div class="invalid-feedback d-block">' src/app/components/campo-error/campo-error.component.html  # expect: exit 0
f=src/app/pages/mascota-form/mascota-form.component.html; test -f $f && grep -qF 'campoId="nombre"' $f && grep -qF 'campoId="raza"' $f && grep -qF 'campoId="edad"' $f && grep -qF 'campoId="fotoUrl"' $f && grep -qF 'campoId="vacunas"' $f && grep -qF '<app-foto-preview' $f && grep -qF '<app-dueno-select' $f  # expect: exit 0
test -f src/app/pages/mascota-form/mascota-form.component.ts && test -f src/app/pages/mascota-form/mascota-form.component.html && test -z "$(grep -nE 'campoInvalido|invalid-feedback' src/app/pages/mascota-form/mascota-form.component.ts src/app/pages/mascota-form/mascota-form.component.html)"  # expect: exit 0
grep -qF '.preview {' src/app/pages/mascota-form/components/foto-preview/foto-preview.component.scss && test -f src/app/pages/mascota-form/mascota-form.component.scss && test -z "$(grep -n 'preview' src/app/pages/mascota-form/mascota-form.component.scss)"  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T2: Formulario de mascota dividido en componentes de campo" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null || git tag dogtor-s04
git rev-parse -q --verify refs/tags/dogtor-s04 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s03`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s03 --staged --worktree -- src && git clean -fd -- src
```

### `E2-T3` — Detalle de mascota dividido en tarjetas presentacionales

**Depends on:** `E2-T2` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 5

**Objetivo:** Después de este paso el detalle se arma con `app-mascota-info-card`, `app-dueno-card` y `app-registro-medico-item`, con nombres resueltos una sola vez, y su HTML tiene como máximo 45 líneas.

Un componente compartido nuevo, `dueno-card` (`src/app/components/dueno-card/`), que pinta nombre, teléfono y dirección con iconos `bi` o "Sin dueño asignado". Dos componentes locales en `src/app/pages/mascota-detail/components/`: `mascota-info-card` (compone `mascota-avatar`, `estado-badge`, la tabla de datos, `dueno-card` y el botón Editar; se lleva la regla `th`) y `registro-medico-item` (solo pinta, con `DatePipe`).

Los componentes hijos son presentacionales: el **padre** resuelve una sola vez, en `ngOnInit`, el nombre del veterinario y los nombres de las drogas y guarda `registros: RegistroVista[]`. No se llaman métodos desde el template que devuelvan arreglos nuevos: cada detección de cambios vería un valor distinto y en modo desarrollo Angular lanza NG0100 (ExpressionChangedAfterItHasBeenChecked). `RegistroMedico` no cambia (sigue con `veterinarioId` y `drogaIds`, ver No-objetivos).

El getter `dueno` del paso 1 y los métodos `getNombreVeterinario`/`getNombresDrogas` públicos desaparecen del detalle.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/components/dueno-card/*`
- `src/app/pages/mascota-detail/components/mascota-info-card/*`
- `src/app/pages/mascota-detail/components/registro-medico-item/*`
- `src/app/pages/mascota-detail/mascota-detail.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se cuentan las líneas de `src/app/pages/mascota-detail/mascota-detail.component.html` **THE SYSTEM SHALL** reportar 45 o menos.
3. **WHEN** se lee `mascota-detail.component.html` **THE SYSTEM SHALL** contener `<app-mascota-info-card` y `<app-registro-medico-item` y cero llamadas a `getNombre`.
4. **WHEN** se lee `mascota-detail.component.ts` **THE SYSTEM SHALL** declarar `registros: RegistroVista[] = [];` y no inyectar `DuenoService`.
5. **WHEN** se lee `src/app/components/dueno-card/dueno-card.component.html` **THE SYSTEM SHALL** contener el texto `Sin dueño asignado` y el componente SHALL declarar `dueno = input<Dueno | undefined>();`.
6. **WHEN** se lee `registro-medico-item.component.ts` **THE SYSTEM SHALL** declarar `registro = input.required<RegistroMedico>();`, `veterinario = input.required<string>();` y `drogas = input<string[]>([]);` sin inyectar ningún servicio.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && test "$(wc -l < src/app/pages/mascota-detail/mascota-detail.component.html)" -le 45  # expect: exit 0 (≤ 45 líneas)
test -f src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-mascota-info-card' src/app/pages/mascota-detail/mascota-detail.component.html && grep -qF '<app-registro-medico-item' src/app/pages/mascota-detail/mascota-detail.component.html && test -z "$(grep -n 'getNombre' src/app/pages/mascota-detail/mascota-detail.component.html)"  # expect: exit 0
test -f src/app/pages/mascota-detail/mascota-detail.component.ts && grep -qF 'registros: RegistroVista[] = [];' src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -n 'DuenoService' src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0
f=src/app/components/dueno-card/dueno-card.component; test -f $f.html && grep -qF 'Sin dueño asignado' $f.html && grep -qF 'dueno = input<Dueno | undefined>();' $f.ts  # expect: exit 0
f=src/app/pages/mascota-detail/components/registro-medico-item/registro-medico-item.component.ts; test -f $f && grep -qF 'registro = input.required<RegistroMedico>();' $f && grep -qF 'veterinario = input.required<string>();' $f && grep -qF 'drogas = input<string[]>([]);' $f && test -z "$(grep -n 'inject(' $f)"  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E2-T3: Detalle de mascota dividido en tarjetas presentacionales" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null || git tag dogtor-s05
git rev-parse -q --verify refs/tags/dogtor-s05 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s04`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s04 --staged --worktree -- src && git clean -fd -- src
```


---

## Epic acceptance

El epic está terminado cuando las tres tareas están `done` **y**:

1. **WHEN** se cuentan las líneas de `mascota-form.component.html` y `mascota-detail.component.html` **THE SYSTEM SHALL** reportar como máximo 70 y 45 respectivamente, con `npx ng build` en exit 0.
2. **WHEN** se busca `fotoPorDefecto` en los `.ts` de `src/app` **THE SYSTEM SHALL** encontrarlo solo en `src/app/components/mascota-avatar/`.

```bash
npx ng build   # expect: exit 0
test "$(wc -l < src/app/pages/mascota-form/mascota-form.component.html)" -le 70 && test "$(wc -l < src/app/pages/mascota-detail/mascota-detail.component.html)" -le 45   # expect: exit 0
test -d src/app && test -z "$(grep -rln fotoPorDefecto src/app --include=*.ts | grep -v '^src/app/components/mascota-avatar/')"   # expect: exit 0
```

## Pitfalls

- **Olvidar `d-block` en `campo-error`** — el campo se pone rojo pero el mensaje nunca aparece.
- **Llamar `getNombresDrogas()` desde el template del hijo** — vuelve NG0100 en modo desarrollo; el padre arma `registros` una sola vez.
- **Inyectar un servicio en `registro-medico-item` o `mascota-info-card`** — rompe la regla presentacional y el gate de E2-T3 (`inject(` prohibido en `registro-medico-item`).
- **Dejar `.foto` o `.preview` en las hojas de estilo de las páginas** — los gates de E2-T1 y E2-T2 lo detectan.
- **Pasar `mascotaForm.get('nombre')` en vez de `mascotaForm.controls.nombre`** — `get()` devuelve `AbstractControl | null` y `strictTemplates` rechaza el binding a `FormControl<string | null>`.

## Before moving on

- [ ] Las tres tareas de este epic están `done` en `tasks.json`; ninguna queda `in_progress`.
- [ ] Pasaron todos los comandos `verify` de cada tarea, no solo el primero.
- [ ] Ningún comando `verify` se editó ni se saltó.
- [ ] Existen las etiquetas `dogtor-s03`, `dogtor-s04` y `dogtor-s05` (`git tag -l 'dogtor-s*'`).
- [ ] El gate del epic pasa desde `dogtor-angular/`.
- [ ] Los contratos "Produced" existen con la firma indicada.
- [ ] No se modificó ningún archivo fuera del subárbol.
- [ ] `.env.example`: no aplica, el proyecto no usa variables de entorno.
- [ ] Un commit por tarea, con el id de la tarea al inicio del mensaje, seguido de su etiqueta.
