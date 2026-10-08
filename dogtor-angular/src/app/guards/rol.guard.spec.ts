import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { Rol } from '../models/usuario.model';
import { AuthService } from '../service/auth.service';
import { authGuard } from './auth.guard';
import { rolGuard } from './rol.guard';

// Control de acceso por rol (AC34) sin backend: AuthService falso con el rol que necesita cada caso
describe('Guards de acceso', () => {
  const rolActual = signal<Rol | null>(null);
  const authFalso = { rol: rolActual, estaLogueado: () => rolActual() !== null };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authFalso }],
    });
  });

  // Ruta con data.roles, como los portales de app.routes.ts
  function ruta(roles: Rol[]) {
    return { data: { roles }, parent: null } as unknown as ActivatedRouteSnapshot;
  }

  function ejecutar(guard: typeof rolGuard, roles: Rol[]) {
    return TestBed.runInInjectionContext(() => guard(ruta(roles), {} as RouterStateSnapshot));
  }

  function url(resultado: unknown) {
    return TestBed.inject(Router).serializeUrl(resultado as UrlTree);
  }

  it('sin sesión, authGuard manda al login', () => {
    rolActual.set(null);
    expect(url(ejecutar(authGuard, ['ADMIN']))).toBe('/login');
  });

  it('sin sesión, rolGuard manda al login', () => {
    rolActual.set(null);
    expect(url(ejecutar(rolGuard, ['ADMIN']))).toBe('/login');
  });

  it('deja entrar al rol permitido', () => {
    rolActual.set('VETERINARIO');
    expect(ejecutar(rolGuard, ['VETERINARIO'])).toBeTrue();
  });

  it('un cliente no entra al portal administrador', () => {
    rolActual.set('DUENO');
    expect(url(ejecutar(rolGuard, ['ADMIN']))).toBe('/acceso-denegado');
  });

  it('en una página hija usa los roles del portal (padre)', () => {
    rolActual.set('ADMIN');
    const hija = { data: {}, parent: ruta(['VETERINARIO']) } as unknown as ActivatedRouteSnapshot;
    const resultado = TestBed.runInInjectionContext(() => rolGuard(hija, {} as RouterStateSnapshot));
    expect(url(resultado)).toBe('/acceso-denegado');
  });
});
