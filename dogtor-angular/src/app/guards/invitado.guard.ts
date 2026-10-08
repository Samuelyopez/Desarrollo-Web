import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

// Para el login: si ya inició sesión lo manda al inicio de su portal
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const usuario = auth.usuario();
  return usuario ? router.createUrlTree([auth.rutaInicio(usuario.rol)]) : true;
};
