import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { PageTitleComponent } from '../../components/page-title/page-title.component';
import { AccesoCardComponent, AccesoPortal } from '../../components/acceso-card/acceso-card.component';
import { AlertaErrorComponent } from '../../components/alerta-error/alerta-error.component';
import { injectPortal } from '../../utils/portal';
import { mensajeError } from '../../utils/mensaje-error';

// Datos de la persona que se saludan en el inicio
interface Persona {
  nombre: string;
  correo?: string;
  activo?: boolean;
}

// Inicio común de los portales cliente, veterinario y administrador.
// El título y los accesos llegan en el data de la ruta (ver app.routes.ts)
@Component({
  selector: 'app-portal-inicio',
  imports: [PageTitleComponent, AccesoCardComponent, AlertaErrorComponent],
  templateUrl: './portal-inicio.component.html',
})
export class PortalInicioComponent {
  //DI
  portal = injectPortal();
  private route = inject(ActivatedRoute);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  titulo: string = this.route.snapshot.data['titulo'];
  // Las rutas de los accesos son relativas: se completan con la base del portal (/vet/800000001/duenos)
  accesos: AccesoPortal[] = ((this.route.snapshot.data['accesos'] ?? []) as AccesoPortal[]).map((acceso) =>
    acceso.ruta ? { ...acceso, ruta: `${this.portal.base}/${acceso.ruta}` } : acceso,
  );

  nombre = '';
  correo = '';
  aviso = '';
  errorMensaje = '';

  constructor() {
    // Sin sesión: el nombre se pide a la API con la cédula de la URL
    let persona$: Observable<Persona> | null = null;
    if (this.portal.tipo === 'cliente') {
      persona$ = this.duenoService.getDuenoByCedula(this.portal.cedula);
    } else if (this.portal.tipo === 'vet') {
      persona$ = this.veterinarioService.getVeterinarioByCedula(this.portal.cedula);
    }
    if (!persona$) {
      this.nombre = 'Administrador';
      return;
    }

    persona$
      .pipe(
        catchError((error: HttpErrorResponse) => {
          this.errorMensaje = mensajeError(error, 'No se pudo cargar el portal.');
          return EMPTY;
        }),
        takeUntilDestroyed(),
      )
      .subscribe((persona) => {
        this.nombre = persona.nombre;
        this.correo = persona.correo ?? '';
        if (persona.activo === false) {
          this.aviso = 'Este veterinario está inactivo (vacaciones o incapacidad): no podrá dar tratamientos.';
        }
      });
  }
}
