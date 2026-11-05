import { Component, Input } from '@angular/core';
import { User } from '../interfaces/interfaces';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  template: `
    <div class="card shadow-sm">
      <div class="row g-0">
        <div class="col-md-4 d-flex align-items-center justify-content-center p-4">
          <img [src]="user.image" [alt]="user.firstName + ' ' + user.lastName" class="rounded-circle img-fluid" style="width: 150px; height: 150px; object-fit: cover;">
        </div>
        <div class="col-md-8">
          <div class="card-body">
            <h3 class="card-title mb-1">{{ user.firstName }} {{ user.lastName }}</h3>
            <p class="text-muted mb-3">&#64;{{ user.username }}</p>
            <div class="row">
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-envelope"></i> Email</small>
                <p class="mb-0">{{ user.email }}</p>
              </div>
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-telephone"></i> Teléfono</small>
                <p class="mb-0">{{ user.phone }}</p>
              </div>
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-calendar"></i> Fecha de nacimiento</small>
                <p class="mb-0">{{ user.birthDate }}</p>
              </div>
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-person"></i> Género</small>
                <p class="mb-0">{{ user.gender }}</p>
              </div>
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-mortarboard"></i> Universidad</small>
                <p class="mb-0">{{ user.university }}</p>
              </div>
              <div class="col-sm-6 mb-2">
                <small class="text-muted"><i class="bi bi-building"></i> Empresa</small>
                <p class="mb-0">{{ user.company.name }}</p>
              </div>
              <div class="col-12 mb-2">
                <small class="text-muted"><i class="bi bi-geo-alt"></i> Dirección</small>
                <p class="mb-0">{{ user.address.address }}, {{ user.address.city }}, {{ user.address.state }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class UserProfileComponent {
  @Input() user!: User;
}
