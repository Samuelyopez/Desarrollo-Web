import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { PageTitleComponent } from '../../components/page-title/page-title.component';
import { AccesoCardComponent, AccesoPortal } from '../../components/acceso-card/acceso-card.component';

// Inicio común de los portales cliente, veterinario y administrador.
// El título y los accesos llegan en el data de la ruta (ver app.routes.ts)
@Component({
  selector: 'app-portal-inicio',
  imports: [PageTitleComponent, AccesoCardComponent],
  templateUrl: './portal-inicio.component.html',
})
export class PortalInicioComponent {
  //DI
  auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  titulo: string = this.route.snapshot.data['titulo'];
  accesos: AccesoPortal[] = this.route.snapshot.data['accesos'] ?? [];
}
