import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { API_URL } from '../../api.config';
import { Tratamiento } from '../../models/tratamiento.model';
import { MiMascotaDetailComponent } from './mi-mascota-detail.component';

// Portal cliente: /cliente/1000000001/mascota/1
describe('MiMascotaDetailComponent', () => {
  let fixture: ComponentFixture<MiMascotaDetailComponent>;
  let http: HttpTestingController;

  const tratamientos: Tratamiento[] = [
    { id: 1, fecha: '2026-10-10', cantidad: 1, precioVenta: 20000, mascotaNombre: 'Max', medicamentoNombre: 'Meloxicam', veterinarioNombre: 'Laura Jiménez' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MiMascotaDetailComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ id: '1' })),
            snapshot: { pathFromRoot: [{ paramMap: convertToParamMap({ cliente: '1000000001' }) }] },
          },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MiMascotaDetailComponent);
  });

  afterEach(() => http.verify());

  it('muestra los tratamientos de la mascota del dueño', () => {
    http.expectOne(`${API_URL}/duenos/1000000001/mascotas/1`).flush({ id: 1, nombre: 'Max', activa: true, duenoCedula: '1000000001' });
    http.expectOne(`${API_URL}/mascotas/1/tratamientos`).flush(tratamientos);
    fixture.detectChanges();

    const filas = fixture.nativeElement.querySelectorAll('app-tratamiento-list tbody tr');
    expect(filas.length).toBe(1);
    expect(filas[0].textContent).toContain('Meloxicam');
  });

  it('no muestra la mascota de otro dueño', () => {
    http.expectOne(`${API_URL}/duenos/1000000001/mascotas/1`).flush({ id: 1, nombre: 'Rocky', activa: true, duenoCedula: '1000000002' });
    http.expectOne(`${API_URL}/mascotas/1/tratamientos`).flush(tratamientos);
    fixture.detectChanges();

    expect(fixture.componentInstance.noEncontrada).toBeTrue();
    expect(fixture.nativeElement.querySelector('app-tratamiento-list')).toBeNull();
  });
});
