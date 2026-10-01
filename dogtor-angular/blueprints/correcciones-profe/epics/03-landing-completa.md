# Epic 03: Landing completa (sin botón de WhatsApp)

> Al terminar este epic, la landing toma todo de `LandingService` y muestra carrusel, servicios, planes, equipo, testimonios, banner y contacto con componentes pequeños; no hay botón de WhatsApp; y el gate final de integración pasa completo.

| | |
|---|---|
| **Epic id** | `03-landing-completa` |
| **Tasks** | `E3-T1` … `E3-T5` (pasos 6–10 de `blueprint.md` §9) |
| **Depends on** | `02-componentes-pequenos` (reutiliza `campo-texto` y `campo-error`) |
| **Unlocks** | nada — es el último epic; E3-T5 es el gate final |
| **Parallel with** | ninguno |

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
  app.component.html|scss                # existe; E3-T1 quita el botón y la regla de WhatsApp
  models/landing.model.ts                # NUEVO en E3-T1
  service/landing.service.ts             # NUEVO en E3-T1 (todos los datos de la landing)
  pages/landing/
    landing.component.ts|html|scss       # existe; E3-T1 a E3-T4 lo reescriben (HTML final ≤ 60 líneas)
    components/hero-carousel/            # NUEVO en E3-T1 (el carrusel actual, sin cambios de comportamiento)
    components/section-header/           # NUEVO en E3-T2
    components/service-card/             # NUEVO en E3-T2
    components/plan-card/                # NUEVO en E3-T2 (output planElegido)
    components/team-card/                # NUEVO en E3-T3
    components/testimonio-card/          # NUEVO en E3-T3
    components/cta-banner/               # NUEVO en E3-T3
    components/contacto-form/            # NUEVO en E3-T4 (output enviado)
  components/campo-texto/, campo-error/  # existen (epic 02); solo lectura
blueprints/correcciones-profe/tasks.json # E3-T5 solo actualiza su status
```

Todo lo demás está fuera de alcance. Si una tarea parece exigir editar un archivo que no está aquí, detente y repórtalo.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| `Slide` | `img`, `title`, `desc`, `link?`, `btn?` | nombres en inglés porque se mueven tal cual del carrusel existente |
| `Servicio` | `icono`, `titulo`, `descripcion` | `icono` es una clase `bi-*` |
| `Plan` | `nombre`, `precio`, `periodo`, `beneficios`, `destacado` | `precio` es texto ya formateado en COP |
| `MiembroEquipo` | `nombre`, `cargo`, `descripcion` | avatar con icono, sin fotos |
| `Testimonio` | `autor`, `mascota`, `texto`, `estrellas` | `estrellas` de 1 a 5 |
| `DatosContacto` | `direccion`, `telefono`, `correo`, `horario` | — |

## Contracts

**Consumed** — ya existe, no lo reconstruyas:

| From | Interface | Guarantee |
|---|---|---|
| `02-componentes-pequenos` | `app-campo-texto` (`campoId`, `etiqueta`, `control: FormControl<string \| null>`, `placeholder`, `mensajes`) | pinta label, input y error |
| `02-componentes-pequenos` | `app-campo-error` (`control: AbstractControl`, `mensajes`) | `invalid-feedback d-block` cuando el control está tocado e inválido |

**Produced** — contratos de este epic:

| Export | Signature | Used by |
|---|---|---|
| `src/app/service/landing.service.ts` → `LandingService` | `getSlides(): Slide[]`, `getServicios(): Servicio[]`, `getPlanes(): Plan[]`, `getEquipo(): MiembroEquipo[]`, `getTestimonios(): Testimonio[]`, `getContacto(): DatosContacto` | `LandingComponent` |
| `plan-card` → `PlanCardComponent` | `plan = input.required<Plan>()`, `planElegido = output<Plan>()` | `LandingComponent.elegirPlan` |
| `contacto-form` → `ContactoFormComponent` | `datos = input.required<DatosContacto>()`, `plan = input<Plan \| undefined>()`, `enviado = output<{ nombre: string; correo: string; mensaje: string }>()` | `LandingComponent.contactoEnviado` |

## Conventions that bite in this area

- El carrusel se mueve **sin cambiar comportamiento**: mismo marcado, misma lógica, mismo SCSS; solo `slides` → `slides()`.
- La entrada de ancla se llama `ancla`, no `id` (Angular duplicaría el id en el host).
- Nada se envía: ni `HttpClient` ni `fetch(` (el gate de E3-T4 lo busca).
- Sin fotos externas nuevas: equipo y testimonios usan iconos de bootstrap-icons.
- Los cuerpos literales del HTML de la landing están medidos: el final tiene 59 líneas; no agregues líneas en blanco extra (límite 60).
- Git **solo con pathspec**: nunca `git reset --hard`, `git stash`, `git add -A` sin pathspec ni `git commit -a`.

Reglas completas: `CLAUDE.md`. Reglas del área: `.claude/rules/angular-componentes.md`. Ambos están en la raíz del proyecto desde el Bootstrap.

---

## Tasks

En el mismo orden que `tasks.json`. Ese orden es el orden de construcción.

### `E3-T1` — LandingService, hero-carousel y fuera el botón de WhatsApp

**Depends on:** `E2-T3` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 6

**Objetivo:** Después de este paso la landing toma sus datos de `LandingService`, el carrusel es `app-hero-carousel` y el botón flotante de WhatsApp ya no existe.

Crea `src/app/models/landing.model.ts` con las interfaces tipadas de la landing y `src/app/service/landing.service.ts` (`providedIn: 'root'`, arreglos quemados como los demás servicios) con **todos** los datos de la landing: las 2 diapositivas actuales (mismas URLs y textos), 6 servicios, 3 planes en pesos colombianos, 3 miembros del equipo, 3 testimonios y los datos de contacto. Los pasos 7–9 solo consumen esos getters.

El carrusel actual se mueve **sin cambiar comportamiento** a `src/app/pages/landing/components/hero-carousel/` (mismo marcado, misma lógica `anterior`/`siguiente`, mismo SCSS copiado tal cual); solo cambia `slides` → `slides()` porque ahora es `input.required<Slide[]>()`.

Se elimina el botón flotante de WhatsApp: el bloque `<a ... class="whatsapp-btn ...">` y su comentario en `app.component.html`, y la regla `.whatsapp-btn` de `app.component.scss`.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/models/landing.model.ts`
- `src/app/service/landing.service.ts`
- `src/app/pages/landing/components/hero-carousel/*`
- `src/app/pages/landing/landing.component.*`
- `src/app/app.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se lee `src/app/service/landing.service.ts` **THE SYSTEM SHALL** declarar `providedIn: 'root'`, los getters `getSlides`, `getServicios`, `getPlanes`, `getEquipo`, `getTestimonios` y `getContacto`, y las dos URLs de diapositiva `photo-1552053831-71594a27632d?w=1200` y `photo-1537151608804-ea6f117f73d2?w=1200`.
3. **WHEN** se busca `unsplash` en `src/app/pages/landing/landing.component.ts` **THE SYSTEM SHALL** encontrar cero coincidencias.
4. **WHEN** se lee `hero-carousel.component.ts` **THE SYSTEM SHALL** declarar `slides = input.required<Slide[]>();` y `landing.component.html` SHALL contener `<app-hero-carousel [slides]="slides">`.
5. **WHEN** se busca `whatsapp` sin distinguir mayúsculas en `src/app` **THE SYSTEM SHALL** encontrar cero coincidencias.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/service/landing.service.ts; test -f $f && grep -qF "providedIn: 'root'" $f && grep -qF 'getSlides()' $f && grep -qF 'getServicios()' $f && grep -qF 'getPlanes()' $f && grep -qF 'getEquipo()' $f && grep -qF 'getTestimonios()' $f && grep -qF 'getContacto()' $f  # expect: exit 0
f=src/app/service/landing.service.ts; grep -qF 'photo-1552053831-71594a27632d?w=1200' $f && grep -qF 'photo-1537151608804-ea6f117f73d2?w=1200' $f  # expect: exit 0
test -f src/app/pages/landing/landing.component.ts && test -z "$(grep -n unsplash src/app/pages/landing/landing.component.ts)"  # expect: exit 0
grep -qF 'slides = input.required<Slide[]>();' src/app/pages/landing/components/hero-carousel/hero-carousel.component.ts && grep -qF '<app-hero-carousel [slides]="slides">' src/app/pages/landing/landing.component.html  # expect: exit 0
test -d src/app && test -z "$(grep -rni whatsapp src/app)"  # expect: exit 0, cero coincidencias
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T1: LandingService, hero-carousel y fuera el botón de WhatsApp" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null || git tag dogtor-s06
git rev-parse -q --verify refs/tags/dogtor-s06 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s05`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s05 --staged --worktree -- src && git clean -fd -- src
```

### `E3-T2` — Secciones de servicios y planes con service-card y plan-card

**Depends on:** `E3-T1` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 7

**Objetivo:** Después de este paso la landing muestra 6 servicios y 3 planes, y elegir un plan lo guarda en `planSeleccionado` y hace scroll a `#contacto`.

Tres componentes locales en `src/app/pages/landing/components/`: `section-header` (entradas `titulo`, `subtitulo` y `ancla` opcional que se pinta como `id` del encabezado; se llama `ancla` y no `id` por la misma razón que `campoId` en el paso 4), `service-card` (entrada `servicio`) y `plan-card` (entrada `plan` y **salida** `planElegido = output<Plan>()`, emitida por su botón).

La landing guarda el plan recibido en `planSeleccionado` y hace `document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' })`. El encabezado con `ancla="contacto"` llega en el paso 9; hasta entonces `getElementById` devuelve `null` y el `?.` lo convierte en un no-op, así que el build y la página siguen funcionando.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/pages/landing/components/section-header/*`
- `src/app/pages/landing/components/service-card/*`
- `src/app/pages/landing/components/plan-card/*`
- `src/app/pages/landing/landing.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se lee `plan-card.component.ts` **THE SYSTEM SHALL** declarar `plan = input.required<Plan>();` y `planElegido = output<Plan>();`.
3. **WHEN** se lee `landing.component.html` **THE SYSTEM SHALL** contener `<app-section-header`, `<app-service-card` y `(planElegido)="elegirPlan($event)"`.
4. **WHEN** se lee `landing.component.ts` **THE SYSTEM SHALL** contener `document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });` y `planSeleccionado: Plan | undefined;`.
5. **WHEN** se lee `src/app/service/landing.service.ts` **THE SYSTEM SHALL** contener los iconos `bi-heart-pulse`, `bi-shield-check`, `bi-scissors`, `bi-capsule`, `bi-house-heart` y `bi-droplet`, y los precios `$49.900`, `$89.900` y `$149.900`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/pages/landing/components/plan-card/plan-card.component.ts; test -f $f && grep -qF 'plan = input.required<Plan>();' $f && grep -qF 'planElegido = output<Plan>();' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-section-header' $f && grep -qF '<app-service-card' $f && grep -qF '(planElegido)="elegirPlan($event)"' $f  # expect: exit 0
f=src/app/pages/landing/landing.component.ts; test -f $f && grep -qF "document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });" $f && grep -qF 'planSeleccionado: Plan | undefined;' $f  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && (for i in bi-heart-pulse bi-shield-check bi-scissors bi-capsule bi-house-heart bi-droplet; do grep -qF "'$i'" $f || exit 1; done)  # expect: exit 0, los 6 iconos
f=src/app/service/landing.service.ts; grep -qF "precio: '\$49.900'" $f && grep -qF "precio: '\$89.900'" $f && grep -qF "precio: '\$149.900'" $f  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T2: Secciones de servicios y planes con service-card y plan-card" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null || git tag dogtor-s07
git rev-parse -q --verify refs/tags/dogtor-s07 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s06`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s06 --staged --worktree -- src && git clean -fd -- src
```

### `E3-T3` — Secciones de equipo, testimonios y banner a la acción

**Depends on:** `E3-T2` · **Priority:** p2 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 8

**Objetivo:** Después de este paso la landing muestra el equipo, los testimonios con estrellas y un banner que lleva a `/mascotas`.

Tres componentes locales en `src/app/pages/landing/components/`: `team-card` (entrada `miembro`; avatar con el icono `bi-person-circle`, sin fotos externas que se puedan romper), `testimonio-card` (entrada `testimonio`; pinta 5 iconos usando un arreglo fijo `posiciones`, `bi-star-fill` para las llenas y `bi-star` para las vacías) y `cta-banner` (entradas `titulo`, `texto`, `textoBoton`, `ruta`; botón con `[routerLink]="ruta()"`).

El equipo son los dos veterinarios que ya existen en `UsuarioService` ("Dr. Andres Felipe" y "Dra. Laura Jimenez") más una persona ficticia de recepción; los datos ya están en `LandingService` desde el paso 6.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/pages/landing/components/team-card/*`
- `src/app/pages/landing/components/testimonio-card/*`
- `src/app/pages/landing/components/cta-banner/*`
- `src/app/pages/landing/landing.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se lee `landing.component.html` **THE SYSTEM SHALL** contener `<app-team-card`, `<app-testimonio-card`, `<app-cta-banner` y `ruta="/mascotas"`.
3. **WHEN** se leen `team-card.component.html` y `testimonio-card.component.html` **THE SYSTEM SHALL** usar `bi-person-circle` y `bi-star-fill` respectivamente.
4. **WHEN** se lee `cta-banner.component.ts` **THE SYSTEM SHALL** declarar `ruta = input.required<string>();` y `cta-banner.component.html` SHALL contener `[routerLink]="ruta()"`.
5. **WHEN** se lee `src/app/service/landing.service.ts` **THE SYSTEM SHALL** contener `Dr. Andres Felipe` y `Dra. Laura Jimenez`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
f=src/app/pages/landing/landing.component.html; test -f $f && grep -qF '<app-team-card' $f && grep -qF '<app-testimonio-card' $f && grep -qF '<app-cta-banner' $f && grep -qF 'ruta="/mascotas"' $f  # expect: exit 0
grep -qF 'bi-person-circle' src/app/pages/landing/components/team-card/team-card.component.html && grep -qF 'bi-star-fill' src/app/pages/landing/components/testimonio-card/testimonio-card.component.html  # expect: exit 0
grep -qF 'ruta = input.required<string>();' src/app/pages/landing/components/cta-banner/cta-banner.component.ts && grep -qF '[routerLink]="ruta()"' src/app/pages/landing/components/cta-banner/cta-banner.component.html  # expect: exit 0
f=src/app/service/landing.service.ts; test -f $f && grep -qF 'Dr. Andres Felipe' $f && grep -qF 'Dra. Laura Jimenez' $f  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T3: Secciones de equipo, testimonios y banner a la acción" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null || git tag dogtor-s08
git rev-parse -q --verify refs/tags/dogtor-s08 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s07`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s07 --staged --worktree -- src && git clean -fd -- src
```

### `E3-T4` — Formulario de contacto con plan precargado (sin envío real)

**Depends on:** `E3-T3` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 9

**Objetivo:** Después de este paso la landing termina en una sección de contacto con los datos de la clínica y un formulario validado que precarga el plan elegido; su HTML tiene como máximo 60 líneas.

Un componente local `contacto-form` en `src/app/pages/landing/components/` con entradas `datos = input.required<DatosContacto>()` y `plan = input<Plan | undefined>()`, y **salida** `enviado`. Formulario reactivo: `nombre` (requerido, mínimo 2), `correo` (requerido, email) y `mensaje` (requerido, mínimo 10). Reutiliza `app-campo-texto` para nombre y correo, y `app-campo-error` para el `<textarea>`.

Se eligió `effect()` (no `ngOnChanges`): en el constructor, cuando `plan()` trae un plan, se hace `setValue("Me interesa el plan <nombre>.")` sobre `mensaje`. Al enviar un formulario válido emite `enviado`, hace `reset()` y muestra la alerta "¡Gracias! Te contactaremos pronto.". **No se envía nada a ningún lado**: no hay backend ni `HttpClient`. La landing limpia `planSeleccionado` al recibir `enviado`.

La sección de contacto es la última de la landing y su encabezado lleva `ancla="contacto"`, que es el destino del scroll del paso 7.

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/pages/landing/components/contacto-form/*`
- `src/app/pages/landing/landing.component.*`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se cuentan las líneas de `src/app/pages/landing/landing.component.html` **THE SYSTEM SHALL** reportar 60 o menos.
3. **WHEN** se lee `landing.component.html` **THE SYSTEM SHALL** contener `<app-hero-carousel`, `<app-service-card`, `<app-plan-card`, `<app-team-card`, `<app-testimonio-card`, `<app-cta-banner`, `<app-contacto-form` y `ancla="contacto"`.
4. **WHEN** se lee `contacto-form.component.ts` **THE SYSTEM SHALL** declarar `enviado = output<{ nombre: string; correo: string; mensaje: string }>();`, usar `effect(` y precargar el texto `Me interesa el plan ${plan.nombre}.`.
5. **WHEN** se lee `contacto-form.component.html` **THE SYSTEM SHALL** contener `¡Gracias! Te contactaremos pronto.`, `<app-campo-texto` y `<app-campo-error`.
6. **WHEN** se buscan `HttpClient` y `fetch(` en `src/app` **THE SYSTEM SHALL** encontrar cero coincidencias.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/pages/landing/landing.component.html && test "$(wc -l < src/app/pages/landing/landing.component.html)" -le 60  # expect: exit 0 (≤ 60 líneas)
f=src/app/pages/landing/landing.component.html; test -f $f && (for t in app-hero-carousel app-service-card app-plan-card app-team-card app-testimonio-card app-cta-banner app-contacto-form; do grep -qF "<$t" $f || exit 1; done) && grep -qF 'ancla="contacto"' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.ts; test -f $f && grep -qF 'enviado = output<{ nombre: string; correo: string; mensaje: string }>();' $f && grep -qF 'effect(' $f && grep -qF 'Me interesa el plan ${plan.nombre}.' $f  # expect: exit 0
f=src/app/pages/landing/components/contacto-form/contacto-form.component.html; test -f $f && grep -qF '¡Gracias! Te contactaremos pronto.' $f && grep -qF '<app-campo-texto' $f && grep -qF '<app-campo-error' $f  # expect: exit 0
test -d src/app && test -z "$(grep -rnE 'HttpClient|fetch\(' src/app)"  # expect: exit 0, nada se envía
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T4: Formulario de contacto con plan precargado (sin envío real)" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null || git tag dogtor-s09
git rev-parse -q --verify refs/tags/dogtor-s09 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s08`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s08 --staged --worktree -- src && git clean -fd -- src
```

### `E3-T5` — Gate final de integración de las correcciones

**Depends on:** `E3-T4` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 10

**Objetivo:** Después de este paso hay evidencia, en un solo bloque ejecutable, de que las tres correcciones conviven: todos los gates de los pasos 1–9 siguen en verde y nada fuera del alcance cambió frente a `dogtor-base`.

Este paso no escribe código de aplicación: ejecuta juntos todos los gates estáticos de los pasos 1–9 (que un paso posterior podría haber roto) más las comprobaciones que solo tienen sentido al final: build sin avisos de presupuesto de estilos, salida del build en `dist/dogtor-angular/browser/`, rutas / `styles.scss` / navbar / footer / `angular.json` / `package.json` / `package-lock.json` sin cambios frente a `dogtor-base`, ningún `*.spec.ts` y las etiquetas de los pasos anteriores presentes.

Si algo falla aquí, **no** se edita el gate: se vuelve al paso cuyo gate falla (su Rollback está en ese paso) y se corrige allí. El checklist visual manual de §20.1 (con `npx ng serve`) se recorre después de este paso y **no** es un gate.

Archivos tocados: ninguno de `src/`. El Checkpoint solo registra `tasks.json` (si cambió su `status`) y crea la etiqueta `dogtor-s10`.

#### Archivos

Ninguno en `src/`. Este paso solo ejecuta gates.

**Files** (`files` en `tasks.json`)

- `blueprints/correcciones-profe/tasks.json`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0 y su salida SHALL no contener la palabra `budget`.
2. **WHEN** se re-ejecutan juntos los comandos `verify` de E1-T1 a E3-T4 **THE SYSTEM SHALL** terminar cada uno con código 0.
3. **WHEN** se compara contra la etiqueta `dogtor-base` **THE SYSTEM SHALL** no mostrar cambios en `src/app/app.routes.ts`, `src/styles.scss`, `src/app/components/navbar`, `src/app/components/footer`, `angular.json`, `package.json` ni `package-lock.json`.
4. **WHEN** se buscan archivos `*.spec.ts` en `src` **THE SYSTEM SHALL** encontrar cero.
5. **WHEN** se consultan las etiquetas de git **THE SYSTEM SHALL** resolver `dogtor-base` y `dogtor-s01` a `dogtor-s09`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

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

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E3-T5: Gate final de integración de las correcciones" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s10 >/dev/null || git tag dogtor-s10
git rev-parse -q --verify refs/tags/dogtor-s10 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s09`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s09 --staged --worktree -- src && git clean -fd -- src
```


---

## Epic acceptance

El epic está terminado cuando las cinco tareas están `done` **y**:

1. **WHEN** se ejecuta el arreglo `verify` de `E3-T5` **THE SYSTEM SHALL** terminar cada línea con código 0.
2. **WHEN** se busca `whatsapp` sin distinguir mayúsculas en `src/app` **THE SYSTEM SHALL** encontrar cero coincidencias.

```bash
npx ng build   # expect: exit 0
test -d src/app && test -z "$(grep -rni whatsapp src/app)"   # expect: exit 0
test "$(wc -l < src/app/pages/landing/landing.component.html)" -le 60   # expect: exit 0
```

El arreglo completo de `E3-T5` (arriba, en su bloque **Verify**) es el gate global de `blueprint.md` §20.1.

## Pitfalls

- **Precargar el mensaje desde el template o con un método** — usa el `effect()` del constructor, tal como está en el cuerpo literal.
- **Poner el scroll en `ngOnInit`** — el scroll va en `elegirPlan()`; antes de E3-T4 no existe `#contacto` y el `?.` lo vuelve un no-op.
- **Copiar el SCSS del carrusel en `landing.component.scss`** — va en `hero-carousel.component.scss`; la landing queda con un comentario de una línea.
- **Editar un gate de E3-T5 porque falla** — se vuelve al paso dueño de ese gate y se corrige allí.

## Before moving on

- [ ] Las cinco tareas de este epic están `done` en `tasks.json`; ninguna queda `in_progress`.
- [ ] Pasaron todos los comandos `verify` de cada tarea, no solo el primero.
- [ ] Ningún comando `verify` se editó ni se saltó.
- [ ] Existen las etiquetas `dogtor-s06` … `dogtor-s10` (`git tag -l 'dogtor-s*'`).
- [ ] El gate del epic pasa desde `dogtor-angular/`.
- [ ] Los contratos "Produced" existen con la firma indicada.
- [ ] No se modificó ningún archivo fuera del subárbol.
- [ ] `.env.example`: no aplica, el proyecto no usa variables de entorno.
- [ ] Un commit por tarea, con el id de la tarea al inicio del mensaje, seguido de su etiqueta.
- [ ] Se recorrió el checklist visual manual de `blueprint.md` §20.1 con `npx ng serve` (no es un gate).
