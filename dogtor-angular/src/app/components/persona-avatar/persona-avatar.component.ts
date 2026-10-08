import { Component, computed, input, signal } from '@angular/core';

// Foto de una persona; si no tiene (o la URL falla) muestra sus iniciales
@Component({
  selector: 'app-persona-avatar',
  imports: [],
  templateUrl: './persona-avatar.component.html',
  styleUrl: './persona-avatar.component.scss',
})
export class PersonaAvatarComponent {
  fotoUrl = input<string | undefined>();
  nombre = input.required<string>();
  tamano = input<number>(48); // ancho y alto en px

  fotoFallida = signal(false);

  // "Laura Jiménez" -> "LJ"
  iniciales = computed(() =>
    this.nombre()
      .split(' ')
      .filter((parte) => parte)
      .slice(0, 2)
      .map((parte) => parte[0].toUpperCase())
      .join(''),
  );
}
