import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError, EMPTY } from 'rxjs';
import { Paciente } from '../../models/paciente.model';
import { AuthService } from '../../service/auth.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';
import { mensajeError } from '../../utils/mensaje-error';

// AC32: mascotas a las que el veterinario logueado les ha dado tratamiento
@Component({
  selector: 'app-mis-pacientes',
  imports: [RouterLink, DatePipe, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './mis-pacientes.component.html',
})
export class MisPacientesComponent {
  //DI
  auth = inject(AuthService);
  private veterinarioService = inject(VeterinarioService);

  pacientes: Paciente[] = [];
  cargando = true;
  errorMensaje = '';

  constructor() {
    // El guard de la ruta garantiza que hay un VETERINARIO logueado; perfilId = id del veterinario
    this.veterinarioService
      .getPacientes(this.auth.usuario()!.perfilId)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          this.errorMensaje = mensajeError(error, 'No se pudieron cargar tus pacientes.');
          this.cargando = false;
          return EMPTY;
        }),
        takeUntilDestroyed(),
      )
      .subscribe((pacientes) => {
        this.pacientes = pacientes;
        this.cargando = false;
      });
  }
}
