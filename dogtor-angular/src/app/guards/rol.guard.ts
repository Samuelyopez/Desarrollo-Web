import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Rol } from '../models/usuario.model';
import { AuthService } from '../service/auth.service';

// Control de acceso por rol: la ruta declara en data.roles quién puede entrar.
// Ej: { path: 'admin', canActivate: [authGuard, rolGuard], data: { roles: ['ADMIN'] } }
// Sirve también como canActivateChild: en una página hija los roles se leen del portal (padre)
export const rolGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const rol = auth.rol();
  if (!rol) {
    return router.createUrlTree(['/login']);
  }

  const rolesPermitidos = (route.data['roles'] ?? route.parent?.data['roles'] ?? []) as Rol[];
  return rolesPermitidos.includes(rol) ? true : router.createUrlTree(['/acceso-denegado']);
};
