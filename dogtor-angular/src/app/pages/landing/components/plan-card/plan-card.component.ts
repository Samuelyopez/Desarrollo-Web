import { Component, input, output } from '@angular/core';
import { Plan } from '../../../../models/landing.model';

@Component({
  selector: 'app-plan-card',
  imports: [],
  templateUrl: './plan-card.component.html',
  styleUrl: './plan-card.component.scss',
})
export class PlanCardComponent {
  // Entrada: el plan a mostrar
  plan = input.required<Plan>();

  // Salida: avisa al padre qué plan eligió el usuario
  planElegido = output<Plan>();

  elegir() {
    this.planElegido.emit(this.plan());
  }
}
