import { Component, input } from '@angular/core';

@Component({
  selector: 'app-mascota-avatar',
  imports: [],
  templateUrl: './mascota-avatar.component.html',
  styleUrl: './mascota-avatar.component.scss',
})
export class MascotaAvatarComponent {
  // Entradas: la foto puede faltar, por eso hay una imagen por defecto
  fotoUrl = input<string | undefined>();
  nombre = input.required<string>();
  tamano = input<number>(56); // ancho y alto en px

  // Antes estaba repetida en la tabla y en el detalle; ahora vive solo aquí
  readonly fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';
}
