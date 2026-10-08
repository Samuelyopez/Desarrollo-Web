import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, EMPTY, forkJoin, map, switchMap, tap } from 'rxjs';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { DuenoCardComponent } from '../../components/dueno-card/dueno-card.component';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-dueno-detail',
  imports: [RouterLink, DuenoCardComponent, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './dueno-detail.component.html',
  styleUrl: './dueno-detail.component.scss',
})
export class DuenoDetailComponent {
  //DI
  private route = inject(ActivatedRoute);
  private duenoService = inject(DuenoService);

  cedula = '';
  dueno: Dueno | undefined;
  mascotas: Mascota[] = [];
  cargando = true;
  noEncontrado = false;
  errorMensaje = '';

  // Mensaje flash: llega desde el formulario de mascota (history.state) al agregarle una mascota
  mensaje = (history.state as { mensaje?: string })?.mensaje ?? '';

  constructor() {
    // paramMap (y no snapshot) para que funcione aunque se navegue de un dueño a otro.
    // forkJoin pide el dueño y sus mascotas en paralelo y emite cuando llegan las dos respuestas
    this.route.paramMap
      .pipe(
        map((params) => params.get('cedula')!),
        tap((cedula) => this.iniciarCarga(cedula)),
        switchMap((cedula) =>
          forkJoin({
            dueno: this.duenoService.getDuenoByCedula(cedula),
            mascotas: this.duenoService.getMascotasByDueno(cedula),
          }).pipe(catchError((error) => this.manejarError(error))),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ dueno, mascotas }) => {
        this.dueno = dueno;
        this.mascotas = mascotas;
        this.cargando = false;
      });
  }

  // Limpia lo del dueño anterior para no mostrar datos de otro
  private iniciarCarga(cedula: string) {
    this.cedula = cedula;
    this.dueno = undefined;
    this.mascotas = [];
    this.noEncontrado = false;
    this.errorMensaje = '';
    this.cargando = true;
  }

  private manejarError(error: HttpErrorResponse) {
    this.cargando = false;
    if (error.status === 404) {
      this.noEncontrado = true;
    } else {
      this.errorMensaje = mensajeError(error, 'No se pudo cargar el dueño.');
    }
    return EMPTY;
  }
}
