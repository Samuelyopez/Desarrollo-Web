import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

// Acceso directo del inicio de un portal. Sin ruta = funcionalidad aún no disponible
export interface AccesoPortal {
  icono: string; // clase de bootstrap-icons
  titulo: string;
  descripcion: string;
  ruta?: string;
}

@Component({
  selector: 'app-acceso-card',
  imports: [RouterLink],
  templateUrl: './acceso-card.component.html',
  styleUrl: './acceso-card.component.scss',
})
export class AccesoCardComponent {
  acceso = input.required<AccesoPortal>();
}
