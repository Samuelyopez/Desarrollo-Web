import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, EMPTY } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaCardComponent } from './components/mascota-card/mascota-card.component';
import { mensajeError } from '../../utils/mensaje-error';
import { injectPortal } from '../../utils/portal';
import { PageTitleComponent } from '../../components/page-title/page-title.component';
import { CargandoComponent } from '../../components/cargando/cargando.component';
import { AlertaErrorComponent } from '../../components/alerta-error/alerta-error.component';
import { EstadoVacioComponent } from '../../components/estado-vacio/estado-vacio.component';

// Portal cliente (AC13): las mascotas del dueño que inició sesión, en tarjetas
@Component({
  selector: 'app-mis-mascotas',
  imports: [MascotaCardComponent, PageTitleComponent, CargandoComponent, AlertaErrorComponent, EstadoVacioComponent],
  templateUrl: './mis-mascotas.component.html',
})
export class MisMascotasComponent {
  //DI
  portal = injectPortal();
  private duenoService = inject(DuenoService);

  mascotas: Mascota[] = [];
  cargando = true;
  errorMensaje = '';

  constructor() {
    // La cédula del dueño viene en la URL (/cliente/<cédula>/mascotas)
    this.duenoService
      .getMascotasByDueno(this.portal.cedula)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          this.errorMensaje = mensajeError(error, 'No se pudieron cargar tus mascotas.');
          this.cargando = false;
          return EMPTY;
        }),
        takeUntilDestroyed(),
      )
      .subscribe((mascotas) => {
        this.mascotas = mascotas;
        this.cargando = false;
      });
  }
}
