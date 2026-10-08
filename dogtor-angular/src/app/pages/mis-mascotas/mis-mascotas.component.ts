import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, EMPTY } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { AuthService } from '../../service/auth.service';
import { DuenoService } from '../../service/dueno.service';
import { MascotaCardComponent } from './components/mascota-card/mascota-card.component';
import { mensajeError } from '../../utils/mensaje-error';

// Portal cliente (AC13): las mascotas del dueño que inició sesión, en tarjetas
@Component({
  selector: 'app-mis-mascotas',
  imports: [MascotaCardComponent],
  templateUrl: './mis-mascotas.component.html',
})
export class MisMascotasComponent {
  //DI
  auth = inject(AuthService);
  private duenoService = inject(DuenoService);

  mascotas: Mascota[] = [];
  cargando = true;
  errorMensaje = '';

  constructor() {
    // El guard de la ruta garantiza que hay un usuario DUENO logueado
    this.duenoService
      .getMascotasByDueno(this.auth.usuario()!.cedula)
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
