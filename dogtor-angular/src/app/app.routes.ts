import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { MascotaTablePageComponent } from './pages/mascota-table-page/mascota-table-page.component';
import { MascotaFormComponent } from './pages/mascota-form/mascota-form.component';
import { MascotaDetailComponent } from './pages/mascota-detail/mascota-detail.component';
import { DuenoTablePageComponent } from './pages/dueno-table-page/dueno-table-page.component';
import { DuenoFormComponent } from './pages/dueno-form/dueno-form.component';
import { DuenoDetailComponent } from './pages/dueno-detail/dueno-detail.component';

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
    path: 'duenos',
    component: DuenoTablePageComponent,
  },
  {
    path: 'dueno/new',
    component: DuenoFormComponent,
  },
  {
    path: 'dueno/update/:id',
    component: DuenoFormComponent,
  },
  {
    path: 'dueno/:id',
    component: DuenoDetailComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
