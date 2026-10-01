import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { MascotaTablePageComponent } from './pages/mascota-table-page/mascota-table-page.component';
import { MascotaFormComponent } from './pages/mascota-form/mascota-form.component';
import { MascotaDetailComponent } from './pages/mascota-detail/mascota-detail.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'mascotas',
    component: MascotaTablePageComponent,
  },
  {
    path: 'mascota/new',
    component: MascotaFormComponent,
  },
  {
    path: 'mascota/update/:id',
    component: MascotaFormComponent,
  },
  {
    path: 'mascota/:id',
    component: MascotaDetailComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
