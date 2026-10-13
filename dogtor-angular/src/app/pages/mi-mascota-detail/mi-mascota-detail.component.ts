import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { catchError, EMPTY, forkJoin, map, switchMap, tap } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { MascotaInfoCardComponent } from '../../components/mascota-info-card/mascota-info-card.component';
import { TratamientoListComponent } from '../../components/tratamiento-list/tratamiento-list.component';
import { mensajeError } from '../../utils/mensaje-error';
import { injectPortal } from '../../utils/portal';
import { VolverLinkComponent } from '../../components/volver-link/volver-link.component';
import { CargandoComponent } from '../../components/cargando/cargando.component';
import { AlertaErrorComponent } from '../../components/alerta-error/alerta-error.component';

// Portal cliente (AC14): detalle de una mascota del dueño con su historial de tratamientos
@Component({
  selector: 'app-mi-mascota-detail',
  imports: [MascotaInfoCardComponent, TratamientoListComponent, VolverLinkComponent, CargandoComponent, AlertaErrorComponent],
  templateUrl: './mi-mascota-detail.component.html',
})
export class MiMascotaDetailComponent {
  //DI
  portal = injectPortal();
  private route = inject(ActivatedRoute);
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  tratamientos: Tratamiento[] = [];
  cargando = true;
  noEncontrada = false;
  errorMensaje = '';

  constructor() {
    // La cédula del dueño viene en la URL (/cliente/<cédula>/mascota/<id>)
    const cedula = this.portal.cedula;

    // forkJoin pide en paralelo la mascota (solo si es del dueño: si no, la API responde 404)
    // y su historial de tratamientos
    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        tap((id) => this.iniciarCarga(id)),
        switchMap((id) =>
          forkJoin({
            mascota: this.duenoService.getMascotaDeDueno(cedula, id),
            tratamientos: this.mascotaService.getTratamientos(id),
          }).pipe(
            // Doble verificación en el front: la mascota debe ser del dueño de la URL
            map((datos) => {
              if (datos.mascota.duenoCedula !== cedula) {
                throw new HttpErrorResponse({ status: 404 });
              }
              return datos;
            }),
            catchError((error) => this.manejarError(error)),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ mascota, tratamientos }) => {
        this.mascota = mascota;
        this.tratamientos = tratamientos;
        this.cargando = false;
      });
  }

  private iniciarCarga(id: number) {
    this.mascotaId = id;
    this.mascota = undefined;
    this.tratamientos = [];
    this.noEncontrada = false;
    this.errorMensaje = '';
    this.cargando = true;
  }

  private manejarError(error: HttpErrorResponse) {
    this.cargando = false;
    if (error.status === 404 || error.status === 400) {
      this.noEncontrada = true;
    } else {
      this.errorMensaje = mensajeError(error, 'No se pudo cargar tu mascota.');
    }
    return EMPTY;
  }
}
