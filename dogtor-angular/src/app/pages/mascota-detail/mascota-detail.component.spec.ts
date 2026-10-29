import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { API_URL } from '../../api.config';
import { Mascota } from '../../models/mascota.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { MascotaDetailComponent } from './mascota-detail.component';

// Portal veterinario: /vet/800000001/mascota/1
describe('MascotaDetailComponent', () => {
  let fixture: ComponentFixture<MascotaDetailComponent>;
  let http: HttpTestingController;

  const tratamientos: Tratamiento[] = [
    { id: 2, fecha: '2026-10-18', cantidad: 2, precioVenta: 15000, mascotaNombre: 'Max', medicamentoNombre: 'Amoxicilina', veterinarioNombre: 'Andrés Felipe Mora' },
    { id: 1, fecha: '2026-10-10', cantidad: 1, precioVenta: 20000, mascotaNombre: 'Max', medicamentoNombre: 'Meloxicam', veterinarioNombre: 'Laura Jiménez' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MascotaDetailComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ id: '1' })),
            snapshot: { pathFromRoot: [{ paramMap: convertToParamMap({ veterinario: '800000001' }) }] },
          },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MascotaDetailComponent);
  });

  afterEach(() => http.verify());

  function cargar(mascota: Mascota, historial: Tratamiento[]) {
    http.expectOne(`${API_URL}/mascotas/1`).flush(mascota);
    http.expectOne(`${API_URL}/mascotas/1/tratamientos`).flush(historial);
    http.expectOne(`${API_URL}/duenos/${mascota.duenoCedula}`).flush({ id: 1, cedula: mascota.duenoCedula, nombre: 'Juan Pérez' });
    fixture.detectChanges();
  }

  it('muestra los tratamientos de la mascota', () => {
    cargar({ id: 1, nombre: 'Max', activa: true, duenoCedula: '1000000001' }, tratamientos);

    const filas = fixture.nativeElement.querySelectorAll('app-tratamiento-list tbody tr');
    expect(filas.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('Historial de tratamientos (2)');
    expect(filas[0].textContent).toContain('Amoxicilina');
    expect(filas[1].textContent).toContain('Laura Jiménez');
  });

  it('si la mascota está activa ofrece dar tratamiento', () => {
    cargar({ id: 1, nombre: 'Max', activa: true, duenoCedula: '1000000001' }, []);

    const enlace: HTMLAnchorElement = fixture.nativeElement.querySelector('a[href*="tratamiento/new"]');
    expect(enlace.getAttribute('href')).toBe('/vet/800000001/tratamiento/new?mascota=1');
    expect(fixture.nativeElement.textContent).toContain('aún no tiene tratamientos');
  });

  it('si la mascota está en casa no deja dar tratamiento', () => {
    cargar({ id: 1, nombre: 'Max', activa: false, duenoCedula: '1000000001' }, tratamientos);

    expect(fixture.nativeElement.querySelector('a[href*="tratamiento/new"]')).toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Está en casa');
  });
});
