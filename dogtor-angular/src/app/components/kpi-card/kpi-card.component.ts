import { Component, input } from '@angular/core';

// Datos de un indicador del dashboard. El valor llega ya formateado (número, moneda o texto)
export interface Kpi {
  icono: string; // clase de bootstrap-icons, ej. "bi-capsule"
  titulo: string;
  valor: string;
  detalle?: string;
  valorEsTexto?: boolean; // el valor es un nombre y no un número: se muestra más pequeño
}

// Tarjeta de un indicador del dashboard
@Component({
  selector: 'app-kpi-card',
  imports: [],
  templateUrl: './kpi-card.component.html',
  styleUrl: './kpi-card.component.scss',
})
export class KpiCardComponent {
  kpi = input.required<Kpi>();
}
