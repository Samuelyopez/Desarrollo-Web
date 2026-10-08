import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, EMPTY, forkJoin, map, switchMap, tap } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { AuthService } from '../../service/auth.service';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { MascotaInfoCardComponent } from '../../components/mascota-info-card/mascota-info-card.component';
import { TratamientoListComponent } from '../../components/tratamiento-list/tratamiento-list.component';
import { mensajeError } from '../../utils/mensaje-error';

// Portal cliente (AC14): detalle de una mascota del dueño con su historial de tratamientos
@Component({
  selector: 'app-mi-mascota-detail',
  imports: [RouterLink, MascotaInfoCardComponent, TratamientoListComponent],
  templateUrl: './mi-mascota-detail.component.html',
})
export class MiMascotaDetailComponent {
  //DI
  private route = inject(ActivatedRoute);
  private auth = inject(AuthService);
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  tratamientos: Tratamiento[] = [];
  cargando = true;
  noEncontrada = false;
  errorMensaje = '';

  constructor() {
    const usuario = this.auth.usuario()!;

    // forkJoin pide en paralelo la mascota (solo si es del dueño: si no, la API responde 404)
    // y su historial de tratamientos
    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        tap((id) => this.iniciarCarga(id)),
        switchMap((id) =>
          forkJoin({
            mascota: this.duenoService.getMascotaDeDueno(usuario.cedula, id),
            tratamientos: this.mascotaService.getTratamientos(id),
          }).pipe(
            // Doble verificación en el front: la mascota debe ser del dueño que inició sesión
            map((datos) => {
              if (datos.mascota.duenoId !== usuario.perfilId) {
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
