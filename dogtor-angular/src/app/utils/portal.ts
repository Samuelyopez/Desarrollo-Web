import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

export type TipoPortal = 'cliente' | 'vet' | 'admin';

// Portal en el que se está y cédula de quien lo usa
export interface Portal {
  tipo: TipoPortal | null;
  cedula: string; // vacía en el portal administrador
  base: string; // ej. "/vet/800000001": los enlaces del portal se arman a partir de aquí
}

// Sin sesión: el portal y la cédula salen de la URL (/cliente/1000000001/..., /vet/800000001/..., /admin/...).
// Así cualquier pantalla se abre solo con su ruta, también al recargar (F5) o con un enlace directo.
// Se usa en un inicializador de campo: portal = injectPortal();
export function injectPortal(): Portal {
  const rutas = inject(ActivatedRoute).snapshot.pathFromRoot;
  for (const ruta of rutas) {
    const cliente = ruta.paramMap.get('cliente');
    if (cliente) {
      return { tipo: 'cliente', cedula: cliente, base: `/cliente/${cliente}` };
    }
    const veterinario = ruta.paramMap.get('veterinario');
    if (veterinario) {
      return { tipo: 'vet', cedula: veterinario, base: `/vet/${veterinario}` };
    }
  }
  const enAdmin = rutas.some((ruta) => ruta.routeConfig?.path === 'admin');
  return enAdmin ? { tipo: 'admin', cedula: '', base: '/admin' } : { tipo: null, cedula: '', base: '' };
}
