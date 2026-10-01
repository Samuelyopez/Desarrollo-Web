import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';

@Component({
  selector: 'app-mascota-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './mascota-form.component.html',
  styleUrl: './mascota-form.component.scss',
})
export class MascotaFormComponent {
  //DI
  mascotaService = inject(MascotaService);
  duenoService = inject(DuenoService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  mascotaId: number | undefined = undefined;
  isEdit = false;
  noEncontrada = false;
  duenos: Dueno[] = [];

  // Se conserva el estado al editar para no reactivar una mascota inactiva
  private activaActual = true;

  mascotaForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    raza: new FormControl('', [Validators.maxLength(50)]),
    edad: new FormControl('', [Validators.maxLength(30)]),
    fotoUrl: new FormControl('', [Validators.pattern(/^https?:\/\/.+/)]),
    vacunas: new FormControl('', [Validators.maxLength(100)]),
    duenoId: new FormControl<number | null>(null, [Validators.required]),
  });

  ngOnInit() {
    this.duenos = this.duenoService.getDuenos();

    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.mascotaId = Number(idParam);

      const mascota = this.mascotaService.getMascotaById(this.mascotaId);
      if (mascota) {
        this.activaActual = mascota.activa;
        this.mascotaForm.patchValue({
          nombre: mascota.nombre,
          raza: mascota.raza ?? '',
          edad: mascota.edad ?? '',
          fotoUrl: mascota.fotoUrl ?? '',
          vacunas: mascota.vacunas ?? '',
          duenoId: mascota.duenoId ?? null,
        });
      } else {
        this.noEncontrada = true;
      }
    }
  }

  // Muestra el error solo cuando el usuario ya tocó el campo
  campoInvalido(campo: string) {
    const control = this.mascotaForm.get(campo);
    return !!control && control.touched && control.invalid;
  }

  handleSubmit() {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      return;
    }

    const formValue = this.mascotaForm.value;

    const mascota: Mascota = {
      id: 0,
      nombre: formValue.nombre!.trim(),
      raza: formValue.raza?.trim() || undefined,
      edad: formValue.edad?.trim() || undefined,
      fotoUrl: formValue.fotoUrl?.trim() || undefined,
      vacunas: formValue.vacunas?.trim() || undefined,
      activa: this.isEdit ? this.activaActual : true,
      duenoId: Number(formValue.duenoId),
    };

    if (this.isEdit) {
      this.mascotaService.updateMascota(this.mascotaId!, mascota);
    } else {
      this.mascotaService.addMascota(mascota);
    }

    this.router.navigate(['/mascotas']);
  }
}
