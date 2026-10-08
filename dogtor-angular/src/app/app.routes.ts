import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { AccesoDenegadoComponent } from './pages/acceso-denegado/acceso-denegado.component';
import { PortalInicioComponent } from './pages/portal-inicio/portal-inicio.component';
import { MascotaTablePageComponent } from './pages/mascota-table-page/mascota-table-page.component';
import { MascotaFormComponent } from './pages/mascota-form/mascota-form.component';
import { MascotaDetailComponent } from './pages/mascota-detail/mascota-detail.component';
import { DuenoTablePageComponent } from './pages/dueno-table-page/dueno-table-page.component';
import { DuenoFormComponent } from './pages/dueno-form/dueno-form.component';
import { DuenoDetailComponent } from './pages/dueno-detail/dueno-detail.component';
import { authGuard } from './guards/auth.guard';
import { rolGuard } from './guards/rol.guard';
import { invitadoGuard } from './guards/invitado.guard';
import { AccesoPortal } from './pages/portal-inicio/portal-inicio.component';

// Accesos del inicio de cada portal (sin ruta = llega en un próximo sprint)
const accesosCliente: AccesoPortal[] = [
  { icono: 'bi-heart', titulo: 'Mis mascotas', descripcion: 'Consulta tus mascotas y sus tratamientos.' },
];

const accesosVeterinario: AccesoPortal[] = [
  { icono: 'bi-people', titulo: 'Dueños', descripcion: 'Registra y gestiona a los clientes.', ruta: '/vet/duenos' },
  { icono: 'bi-heart-pulse', titulo: 'Mascotas', descripcion: 'Pacientes de la clínica.', ruta: '/vet/mascotas' },
  { icono: 'bi-capsule', titulo: 'Tratamientos', descripcion: 'Suministra medicamentos a las mascotas activas.' },
  { icono: 'bi-clipboard2-pulse', titulo: 'Mis pacientes', descripcion: 'Mascotas a las que les has dado tratamiento.' },
];

const accesosAdmin: AccesoPortal[] = [
  { icono: 'bi-graph-up', titulo: 'Dashboard', descripcion: 'Indicadores del negocio.' },
  { icono: 'bi-person-badge', titulo: 'Veterinarios', descripcion: 'Registra, edita y activa veterinarios.' },
];

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [invitadoGuard],
  },
  {
    path: 'acceso-denegado',
    component: AccesoDenegadoComponent,
  },

  // Portal cliente (dueño)
  {
    path: 'cliente',
    canActivate: [authGuard, rolGuard],
    data: { roles: ['DUENO'] },
    children: [
      { path: '', component: PortalInicioComponent, data: { titulo: 'Portal cliente', accesos: accesosCliente } },
    ],
  },

  // Portal veterinario
  {
    path: 'vet',
    canActivate: [authGuard, rolGuard],
    data: { roles: ['VETERINARIO'] },
    children: [
      { path: '', component: PortalInicioComponent, data: { titulo: 'Portal veterinario', accesos: accesosVeterinario } },
      { path: 'mascotas', component: MascotaTablePageComponent },
      { path: 'mascota/new', component: MascotaFormComponent },
      { path: 'mascota/update/:id', component: MascotaFormComponent },
      { path: 'mascota/:id', component: MascotaDetailComponent },
      { path: 'duenos', component: DuenoTablePageComponent },
      { path: 'dueno/new', component: DuenoFormComponent },
      { path: 'dueno/update/:cedula', component: DuenoFormComponent },
      { path: 'dueno/:cedula', component: DuenoDetailComponent },
    ],
  },

  // Portal administrador
  {
    path: 'admin',
    canActivate: [authGuard, rolGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', component: PortalInicioComponent, data: { titulo: 'Portal administrador', accesos: accesosAdmin } },
    ],
  },

  {
    path: '**',
    redirectTo: '',
  },
];
