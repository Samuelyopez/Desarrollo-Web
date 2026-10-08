import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../service/auth.service';

// Acceso directo del inicio de un portal. Sin ruta = funcionalidad de un próximo sprint
export interface AccesoPortal {
  icono: string; // clase de bootstrap-icons
  titulo: string;
  descripcion: string;
  ruta?: string;
}

// Inicio común de los portales cliente, veterinario y administrador.
// El título y los accesos llegan en el data de la ruta (ver app.routes.ts)
@Component({
  selector: 'app-portal-inicio',
  imports: [RouterLink],
  templateUrl: './portal-inicio.component.html',
  styleUrl: './portal-inicio.component.scss',
})
export class PortalInicioComponent {
  //DI
  auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  titulo: string = this.route.snapshot.data['titulo'];
  accesos: AccesoPortal[] = this.route.snapshot.data['accesos'] ?? [];
}
