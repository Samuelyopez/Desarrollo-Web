import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CampoErrorComponent } from '../campo-error/campo-error.component';

@Component({
  selector: 'app-campo-texto',
  imports: [ReactiveFormsModule, CampoErrorComponent],
  templateUrl: './campo-texto.component.html',
  styleUrl: './campo-texto.component.scss',
})
export class CampoTextoComponent {
  // Entradas. Se llama campoId (no "id") para que Angular no ponga el mismo id en el host y en el <input>
  campoId = input.required<string>();
  etiqueta = input.required<string>();
  control = input.required<FormControl<string | null>>();
  placeholder = input<string>('');
  tipo = input<'text' | 'email' | 'password'>('text');
  mensajes = input<Record<string, string>>({});
}
