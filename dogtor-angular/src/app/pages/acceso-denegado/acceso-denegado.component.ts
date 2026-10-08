import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-acceso-denegado',
  imports: [RouterLink],
  templateUrl: './acceso-denegado.component.html',
})
export class AccesoDenegadoComponent {
  //DI
  private auth = inject(AuthService);

  // El botón lleva al inicio del portal del usuario (o a la landing si no hay sesión)
  rutaInicio = computed(() => {
    const rol = this.auth.rol();
    return rol ? this.auth.rutaInicio(rol) : '/';
  });
}
