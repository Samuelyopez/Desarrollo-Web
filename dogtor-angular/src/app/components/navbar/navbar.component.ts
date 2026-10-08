import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { Rol } from '../../models/usuario.model';

interface LinkNavbar {
  texto: string;
  ruta: string;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  //DI
  auth = inject(AuthService);
  private router = inject(Router);

  // Cada rol ve solo los enlaces de su portal
  private readonly linksPorRol: Record<Rol, LinkNavbar[]> = {
    DUENO: [
      { texto: 'Mi portal', ruta: '/cliente' },
      { texto: 'Mis mascotas', ruta: '/cliente/mascotas' },
    ],
    VETERINARIO: [
      { texto: 'Mi portal', ruta: '/vet' },
      { texto: 'Dueños', ruta: '/vet/duenos' },
      { texto: 'Mascotas', ruta: '/vet/mascotas' },
      { texto: 'Mis pacientes', ruta: '/vet/pacientes' },
    ],
    ADMIN: [
      { texto: 'Mi portal', ruta: '/admin' },
      { texto: 'Dashboard', ruta: '/admin/dashboard' },
      { texto: 'Veterinarios', ruta: '/admin/veterinarios' },
    ],
  };

  // Se recalcula solo cuando cambia el rol (al entrar o salir)
  links = computed(() => {
    const rol = this.auth.rol();
    return [{ texto: 'Inicio', ruta: '/' }, ...(rol ? this.linksPorRol[rol] : [])];
  });

  readonly nombreRol: Record<Rol, string> = {
    DUENO: 'Cliente',
    VETERINARIO: 'Veterinario',
    ADMIN: 'Administrador',
  };

  salir() {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}
