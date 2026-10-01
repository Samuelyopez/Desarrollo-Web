# Epic 01: Mascota guarda su Dueno como objeto

> Al terminar este epic, `Mascota.dueno` es un objeto `Dueno`, `duenoId` no existe en `src/app` y ningún componente resuelve ids de dueño.

| | |
|---|---|
| **Epic id** | `01-mascota-dueno-objeto` |
| **Tasks** | `E1-T1` … `E1-T2` (pasos 1–2 de `blueprint.md` §9) |
| **Depends on** | nada — empieza aquí, después del Bootstrap de `blueprint.md` §10 |
| **Unlocks** | `02-componentes-pequenos` |
| **Parallel with** | ninguno (los tres epics forman una cadena: cada uno edita archivos que el siguiente reescribe) |

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
  models/
    mascota.model.ts                     # existe; E1-T1 agrega dueno, E1-T2 quita duenoId
    dueno.model.ts                       # existe; solo lectura
  service/
    mascota.service.ts                   # existe; E1-T1 y E1-T2 cambian la semilla
    dueno.service.ts                     # existe; solo lectura (getDuenoById / getDuenos)
  pages/
    mascota-table-page/components/mascota-table/
      mascota-table.component.ts         # existe; E1-T1 quita DuenoService y getNombreDueno
      mascota-table.component.html       # existe; E1-T1 usa mascota.dueno?.nombre
    mascota-detail/
      mascota-detail.component.ts        # existe; E1-T1 quita DuenoService (getter dueno)
    mascota-form/
      mascota-form.component.ts          # existe; E1-T2 FormControl dueno + compareWith
      mascota-form.component.html        # existe; E1-T2 bloque Dueño
```

Todo lo demás está fuera de alcance. Si una tarea parece exigir editar un archivo que no está aquí, detente y repórtalo.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| `Mascota` | agrega `dueno?: Dueno`; quita `duenoId?: number` (E1-T2) | `@ManyToOne` con `Dueno`, guardado como objeto |
| `Dueno` | lee `id`, `nombre` | sin cambios; la instancia es la de `DuenoService` |

## Contracts

**Consumed** — ya existe, no lo reconstruyas:

| From | Interface | Guarantee |
|---|---|---|
| repo existente | `DuenoService.getDuenoById(id: number)` | devuelve el `Dueno` del arreglo del servicio (misma instancia) o `undefined` |
| repo existente | `DuenoService.getDuenos()` | devuelve el arreglo de 10 dueños |

**Produced** — los epics siguientes dependen de estas firmas:

| Export | Signature | Used by |
|---|---|---|
| `src/app/models/mascota.model.ts` → `Mascota` | `{ id: number; nombre: string; raza?: string; edad?: string; fotoUrl?: string; vacunas?: string; activa: boolean; dueno?: Dueno }` | `02-componentes-pequenos` |
| `src/app/service/mascota.service.ts` → `getMascotasByDueno` | `(dueno: Dueno) => Mascota[]` | nadie todavía |
| `mascota-form.component.ts` → `mascotaForm.controls.dueno` | `FormControl<Dueno \| null>` | `02-componentes-pequenos` (E2-T2) |

## Conventions that bite in this area

- Los inicializadores de campos corren en orden: `private duenoService = inject(DuenoService);` va **antes** de `private mascotaArray`.
- `//DI` encima de cada `inject()`; identificadores y comentarios en español.
- Git **solo con pathspec**: el repo padre tiene cambios sin commit de `Proyecto-Veterinaria-2.0/`. Nunca `git reset --hard`, `git stash`, `git add -A` sin pathspec ni `git commit -a`.
- Cuerpos completos: cada archivo de la lista **Archivos** se escribe con el contenido literal de abajo; nada fuera de esa lista se toca.

Reglas completas: `CLAUDE.md`. Reglas del área: `.claude/rules/angular-componentes.md`. Ambos están en la raíz del proyecto desde el Bootstrap.

---

## Tasks

En el mismo orden que `tasks.json`. Ese orden es el orden de construcción.

### `E1-T1` — Mascota guarda el objeto Dueno (coexiste con duenoId)

**Depends on:** nada (primera tarea) · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 1

**Objetivo:** Después de este paso cada `Mascota` lleva su `dueno` como objeto y ni la tabla ni el detalle resuelven ids de dueño.

Primer paso del cambio incremental de `duenoId` a `dueno`. `Mascota` gana `dueno?: Dueno` y **conserva temporalmente** `duenoId?: number` (lo sigue usando el formulario hasta el paso 2), así el build nunca queda en rojo.

`MascotaService` inyecta `DuenoService` **antes** de declarar `mascotaArray` (los inicializadores de campos se ejecutan en orden de declaración) y cada una de las filas semilla pasa a llevar `dueno: this.duenoService.getDuenoById(N)` con el mismo `N` que su `duenoId`. Así el objeto `Dueno` de la mascota es **la misma instancia** que guarda `DuenoService`, que es lo que el `compareWith` del paso 2 necesita. Resolver el id dentro del seed del servicio es aceptable: la queja del profesor era que los *componentes* resolvían ids.

La tabla deja de inyectar `DuenoService` y pierde `getNombreDueno`; muestra `mascota.dueno?.nombre`. El detalle deja de inyectar `DuenoService`: su propiedad `dueno` pasa a ser un *getter* que lee `this.mascota?.dueno`, por eso su HTML no cambia en este paso (se rehace en el paso 5).

Estado transitorio conocido: entre este paso y el paso 2, una mascota guardada desde el formulario queda con `duenoId` pero sin `dueno`, y la tabla muestra "—" para ella. Se corrige en el paso 2; no hay persistencia (F5 recarga la semilla).

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/models/mascota.model.ts`
- `src/app/service/mascota.service.ts`
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts`
- `src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html`
- `src/app/pages/mascota-detail/mascota-detail.component.ts`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se lee `src/app/models/mascota.model.ts` **THE SYSTEM SHALL** contener la línea de campo `dueno?: Dueno;` y el import de `Dueno` desde `./dueno.model`.
3. **WHEN** se lee `src/app/service/mascota.service.ts` **THE SYSTEM SHALL** declarar `private duenoService = inject(DuenoService);` en una línea anterior a `private mascotaArray: Mascota[] = [`.
4. **WHEN** se comparan las filas semilla de `src/app/service/mascota.service.ts` con las de la etiqueta `dogtor-base` **THE SYSTEM SHALL** mostrar, fila por fila y en el mismo orden, `dueno: this.duenoService.getDuenoById(N) }` con el mismo `N` que tenía `duenoId: N }`.
5. **WHEN** se buscan `DuenoService`, `getDuenoById` y `getNombreDueno` en `mascota-table.component.ts`, `mascota-table.component.html` y `mascota-detail.component.ts` **THE SYSTEM SHALL** encontrar cero coincidencias.
6. **WHEN** se lee `mascota-table.component.html` **THE SYSTEM SHALL** mostrar el dueño con `{{ mascota.dueno?.nombre ?? '—' }}`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -f src/app/models/mascota.model.ts && grep -qF 'dueno?: Dueno;' src/app/models/mascota.model.ts && grep -qF "import { Dueno } from './dueno.model';" src/app/models/mascota.model.ts  # expect: exit 0
test "$(grep -n 'private duenoService = inject(DuenoService);' src/app/service/mascota.service.ts | cut -d: -f1)" -lt "$(grep -n 'private mascotaArray: Mascota\[\] = \[' src/app/service/mascota.service.ts | cut -d: -f1)"  # expect: exit 0 (duenoService se declara antes del arreglo)
a="$(git show dogtor-base:./src/app/service/mascota.service.ts | grep -oE 'duenoId: [0-9]+ \}' | grep -oE '[0-9]+')"; b="$(grep -oE 'getDuenoById\([0-9]+\) \}' src/app/service/mascota.service.ts | grep -oE '[0-9]+')"; test -n "$a" && test "$a" = "$b"  # expect: exit 0 (cada fila usa el mismo N que tenía en dogtor-base)
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts && test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && test -f src/app/pages/mascota-detail/mascota-detail.component.ts && test -z "$(grep -nE 'DuenoService|getDuenoById|getNombreDueno' src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.ts src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html src/app/pages/mascota-detail/mascota-detail.component.ts)"  # expect: exit 0, sin coincidencias
test -f src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html && grep -qF "{{ mascota.dueno?.nombre ?? '—' }}" src/app/pages/mascota-table-page/components/mascota-table/mascota-table.component.html  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E1-T1: Mascota guarda el objeto Dueno (coexiste con duenoId)" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null || git tag dogtor-s01
git rev-parse -q --verify refs/tags/dogtor-s01 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-base`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-base --staged --worktree -- src && git clean -fd -- src
```

### `E1-T2` — Formulario usa el objeto Dueno y se elimina duenoId

**Depends on:** `E1-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso del blueprint:** 2

**Objetivo:** Después de este paso `duenoId` no existe en `src/app` y el formulario guarda y precarga el objeto `Dueno`.

Cierra la coexistencia. El `FormControl` `duenoId` pasa a llamarse `dueno` y guarda un `Dueno | null`; las opciones del `<select>` usan `[ngValue]="dueno"` (el objeto) y el `<select>` recibe `[compareWith]="compararDuenos"` para que, al editar, quede seleccionado el dueño actual aunque se compare por id.

`Mascota` pierde `duenoId`; las filas semilla del servicio pierden `duenoId: N, ` y quedan solo con `dueno: this.duenoService.getDuenoById(N)`. `getMascotasByDueno` recibe ahora el objeto (`dueno: Dueno`) y filtra por `m.dueno?.id === dueno.id`: se renombró el parámetro porque el gate exige cero ocurrencias de `duenoId` en `src/app`, y el método no tiene llamadores hoy.

En el HTML del formulario solo cambia el bloque "Dueño" (el resto queda idéntico; se da el archivo completo para no adivinar).

#### Archivos

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

**Files** (`files` en `tasks.json`)

- `src/app/models/mascota.model.ts`
- `src/app/service/mascota.service.ts`
- `src/app/pages/mascota-form/mascota-form.component.ts`
- `src/app/pages/mascota-form/mascota-form.component.html`

**Acceptance**

Copiado literal del arreglo `acceptance` de esta tarea en `tasks.json`. Cada criterio lo decide un comando de abajo, en esta máquina, durante el build.

1. **WHEN** se ejecuta `npx ng build` **THE SYSTEM SHALL** terminar con código 0.
2. **WHEN** se busca el texto `duenoId` en `src/app` **THE SYSTEM SHALL** encontrar cero coincidencias.
3. **WHEN** se busca `getDuenoById` en los archivos `.ts` de `src/app` fuera de `src/app/service/` **THE SYSTEM SHALL** encontrar cero coincidencias.
4. **WHEN** se lee `mascota-form.component.ts` **THE SYSTEM SHALL** declarar `dueno: new FormControl<Dueno | null>(null, [Validators.required]),`, precargar con `dueno: mascota.dueno ?? null,` y guardar con `dueno: formValue.dueno ?? undefined,`.
5. **WHEN** se busca en `src/app/pages/mascota-form/` **THE SYSTEM SHALL** encontrar `compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);`, `[compareWith]="compararDuenos"` y `<option [ngValue]="dueno">`.
6. **WHEN** se lee `src/app/service/mascota.service.ts` **THE SYSTEM SHALL** filtrar `getMascotasByDueno` con `m.dueno?.id === dueno.id`.

**Verify** — cada comando, en orden, desde `dogtor-angular/`. Es el arreglo `verify` de `tasks.json`, una línea por elemento. La tarea queda `done` cuando la última línea termina con exit 0.

```bash
npx ng build  # expect: exit 0 (el WARNING "4 rules skipped due to selector errors" de Bootstrap es esperado y ya existía)
test -d src/app && test -z "$(grep -rn duenoId src/app)"  # expect: exit 0, cero ocurrencias
test -d src/app && test -z "$(grep -rn getDuenoById src/app --include=*.ts | grep -v '^src/app/service/')"  # expect: exit 0, solo se usa dentro de src/app/service/
test -f src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: new FormControl<Dueno | null>(null, [Validators.required]),' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: mascota.dueno ?? null,' src/app/pages/mascota-form/mascota-form.component.ts && grep -qF 'dueno: formValue.dueno ?? undefined,' src/app/pages/mascota-form/mascota-form.component.ts  # expect: exit 0
test -d src/app/pages/mascota-form && grep -rqF 'compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);' src/app/pages/mascota-form && grep -rqF '[compareWith]="compararDuenos"' src/app/pages/mascota-form && grep -rqF '<option [ngValue]="dueno">' src/app/pages/mascota-form  # expect: exit 0
test -f src/app/service/mascota.service.ts && grep -qF 'm.dueno?.id === dueno.id' src/app/service/mascota.service.ts  # expect: exit 0
```

**Checkpoint** — después de que la última línea de Verify termine con exit 0 y antes de empezar la siguiente tarea. La etiqueta es el campo `checkpoint` de `tasks.json`.

```bash
git add -A -- src blueprints
git diff --cached --quiet -- src blueprints || git commit -m "E1-T2: Formulario usa el objeto Dueno y se elimina duenoId" -- src blueprints
git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null || git tag dogtor-s02
git rev-parse -q --verify refs/tags/dogtor-s02 >/dev/null   # expect: exit 0 — la etiqueta existe (aserción posterior al commit)
```

**Rollback** — vuelve al estado verificado anterior (`dogtor-s01`); nunca `git reset --hard`:

```bash
git restore --source=dogtor-s01 --staged --worktree -- src && git clean -fd -- src
```


---

## Epic acceptance

El epic está terminado cuando las dos tareas están `done` **y**:

1. **WHEN** se busca `duenoId` en `src/app` **THE SYSTEM SHALL** encontrar cero coincidencias y `npx ng build` SHALL terminar con código 0.
2. **WHEN** se busca `getDuenoById` en los `.ts` de `src/app` fuera de `src/app/service/` **THE SYSTEM SHALL** encontrar cero coincidencias.

```bash
npx ng build   # expect: exit 0
test -d src/app && test -z "$(grep -rn duenoId src/app)"   # expect: exit 0
test -d src/app && test -z "$(grep -rn getDuenoById src/app --include=*.ts | grep -v '^src/app/service/')"   # expect: exit 0
```

## Pitfalls

- **Declarar `duenoService` después de `mascotaArray`** — el arreglo se inicializa con `this.duenoService` indefinido y la app falla al cargar. Va antes, como en el cuerpo literal.
- **`[ngValue]="dueno.id"` en vez de `[ngValue]="dueno"`** — el control volvería a guardar un número y `compareWith` recibiría tipos distintos.
- **Quitar `compareWith`** — al editar, el `<select>` aparece vacío si alguna vez el objeto no es la misma instancia.
- **El texto "—" de la tabla entre E1-T1 y E1-T2** — una mascota guardada desde el formulario en ese intervalo queda sin `dueno`. Es esperado; E1-T2 lo cierra.

## Before moving on

- [ ] Las dos tareas de este epic están `done` en `tasks.json`; ninguna queda `in_progress`.
- [ ] Pasaron todos los comandos `verify` de cada tarea, no solo el primero.
- [ ] Ningún comando `verify` se editó ni se saltó.
- [ ] Existen las etiquetas `dogtor-s01` y `dogtor-s02` (`git tag -l 'dogtor-s*'`).
- [ ] El gate del epic pasa desde `dogtor-angular/`.
- [ ] Los contratos "Produced" existen con la firma indicada.
- [ ] No se modificó ningún archivo fuera del subárbol.
- [ ] `.env.example`: no aplica, el proyecto no usa variables de entorno.
- [ ] Un commit por tarea, con el id de la tarea al inicio del mensaje, seguido de su etiqueta.
