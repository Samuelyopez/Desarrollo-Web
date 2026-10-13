import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError, EMPTY, switchMap } from 'rxjs';
import { Paciente } from '../../models/paciente.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';
import { mensajeError } from '../../utils/mensaje-error';
import { injectPortal } from '../../utils/portal';
import { PageTitleComponent } from '../../components/page-title/page-title.component';
import { CargandoComponent } from '../../components/cargando/cargando.component';
import { AlertaErrorComponent } from '../../components/alerta-error/alerta-error.component';
import { EstadoVacioComponent } from '../../components/estado-vacio/estado-vacio.component';

// AC32: mascotas a las que el veterinario del portal les ha dado tratamiento
@Component({
  selector: 'app-mis-pacientes',
  imports: [RouterLink, DatePipe, MascotaAvatarComponent, EstadoBadgeComponent, PageTitleComponent, CargandoComponent, AlertaErrorComponent, EstadoVacioComponent],
  templateUrl: './mis-pacientes.component.html',
})
export class MisPacientesComponent {
  //DI
  portal = injectPortal();
  private veterinarioService = inject(VeterinarioService);

  pacientes: Paciente[] = [];
  cargando = true;
  errorMensaje = '';

  constructor() {
    // La cédula del veterinario viene en la URL; "Mis pacientes" se pide con su id interno
    this.veterinarioService
      .getVeterinarioByCedula(this.portal.cedula)
      .pipe(
        switchMap((veterinario) => this.veterinarioService.getPacientes(veterinario.id)),
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
