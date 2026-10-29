import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, map } from 'rxjs';
import { TipoPortal } from '../../utils/portal';

interface LinkNavbar {
  texto: string;
  ruta: string; // relativa al portal; '' = inicio del portal
}

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  //DI
  private router = inject(Router);

  // Sin sesión: el portal se deduce de la URL actual (/vet/800000001/... -> portal veterinario)
  private url = toSignal(
    this.router.events.pipe(
      filter((evento): evento is NavigationEnd => evento instanceof NavigationEnd),
      map((evento) => evento.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  portal = computed(() => {
    const [tipo, cedula] = this.url().split(/[?#]/)[0].split('/').filter(Boolean);
    if ((tipo === 'cliente' || tipo === 'vet') && cedula) {
      return { tipo: tipo as TipoPortal, base: `/${tipo}/${cedula}` };
    }
    if (tipo === 'admin') {
      return { tipo: 'admin' as TipoPortal, base: '/admin' };
    }
    return null;
  });

  // Cada portal muestra solo sus enlaces
  private readonly linksPorPortal: Record<TipoPortal, LinkNavbar[]> = {
    cliente: [
      { texto: 'Mi portal', ruta: '' },
      { texto: 'Mis mascotas', ruta: 'mascotas' },
    ],
    vet: [
      { texto: 'Mi portal', ruta: '' },
      { texto: 'Dueños', ruta: 'duenos' },
      { texto: 'Mascotas', ruta: 'mascotas' },
      { texto: 'Mis pacientes', ruta: 'pacientes' },
    ],
    admin: [
      { texto: 'Mi portal', ruta: '' },
      { texto: 'Dashboard', ruta: 'dashboard' },
      { texto: 'Veterinarios', ruta: 'veterinarios' },
    ],
  };

  readonly nombrePortal: Record<TipoPortal, string> = {
    cliente: 'Portal cliente',
    vet: 'Portal veterinario',
    admin: 'Portal administrador',
  };

  // Se recalcula solo cuando cambia el portal de la URL
  links = computed(() => {
    const portal = this.portal();
    const inicio = [{ texto: 'Inicio', ruta: '/' }];
    if (!portal) {
      return inicio;
    }
    return [
      ...inicio,
      ...this.linksPorPortal[portal.tipo].map((link) => ({
        texto: link.texto,
        ruta: link.ruta ? `${portal.base}/${link.ruta}` : portal.base,
      })),
    ];
  });

  salir() {
    this.router.navigateByUrl('/');
  }
}
