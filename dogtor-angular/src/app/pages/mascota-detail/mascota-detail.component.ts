import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, EMPTY, forkJoin, map, switchMap, tap } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { MascotaInfoCardComponent } from '../../components/mascota-info-card/mascota-info-card.component';
import { TratamientoListComponent } from '../../components/tratamiento-list/tratamiento-list.component';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, MascotaInfoCardComponent, TratamientoListComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  private route = inject(ActivatedRoute);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  dueno: Dueno | undefined;
  tratamientos: Tratamiento[] = [];
  cargando = true;
  noEncontrada = false;
  errorMensaje = '';

  // Mensaje flash: llega desde el formulario de tratamiento (history.state)
  mensaje = (history.state as { mensaje?: string })?.mensaje ?? '';

  constructor() {
    // forkJoin en paralelo: (1) la mascota y luego su dueño (consulta anidada con switchMap,
    // sin subscribes anidados) y (2) su historial de tratamientos
    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        tap((id) => this.iniciarCarga(id)),
        switchMap((id) =>
          forkJoin({
            datos: this.mascotaService.getMascotaById(id).pipe(
              switchMap((mascota) =>
                this.duenoService.getDuenoByCedula(mascota.duenoCedula!).pipe(map((dueno) => ({ mascota, dueno }))),
              ),
            ),
            tratamientos: this.mascotaService.getTratamientos(id),
          }).pipe(catchError((error) => this.manejarError(error))),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ datos, tratamientos }) => {
        this.mascota = datos.mascota;
        this.dueno = datos.dueno;
        this.tratamientos = tratamientos;
        this.cargando = false;
      });
  }

  // Limpia lo de la mascota anterior para no mostrar datos de otra
  private iniciarCarga(id: number) {
    this.mascotaId = id;
    this.mascota = undefined;
    this.dueno = undefined;
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
      this.errorMensaje = mensajeError(error, 'No se pudo cargar la mascota.');
    }
    return EMPTY;
  }
}
