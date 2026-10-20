import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { API_URL } from '../../api.config';
import { Mascota } from '../../models/mascota.model';
import { Medicamento } from '../../models/medicamento.model';
import { Veterinario } from '../../models/veterinario.model';
import { TratamientoFormComponent } from './tratamiento-form.component';

// Se entra como /vet/800000001/tratamiento/new?mascota=1 (la cédula del portal va en la URL)
describe('TratamientoFormComponent', () => {
  let fixture: ComponentFixture<TratamientoFormComponent>;
  let component: TratamientoFormComponent;
  let http: HttpTestingController;

  const veterinario: Veterinario = { id: 1, cedula: '800000001', nombre: 'Andrés Felipe Mora', activo: true };
  const mascotas: Mascota[] = [{ id: 1, nombre: 'Max', activa: true, duenoNombre: 'Juan Pérez' }];
  const amoxicilina: Medicamento = { id: 10, nombre: 'Amoxicilina', precioVenta: 15000, unidadesDisponibles: 3, unidadesVendidas: 0 };
  const meloxicam: Medicamento = { id: 11, nombre: 'Meloxicam', precioVenta: 20000, unidadesDisponibles: 8, unidadesVendidas: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TratamientoFormComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: { mascota: '1' },
              pathFromRoot: [{ paramMap: convertToParamMap({ veterinario: '800000001' }) }],
            },
          },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(TratamientoFormComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => http.verify());

  // Responde las tres peticiones del forkJoin inicial
  function cargar(medicamentos: Medicamento[]) {
    http.expectOne(`${API_URL}/veterinarios/800000001`).flush(veterinario);
    http.expectOne((r) => r.url === `${API_URL}/mascotas`).flush(mascotas);
    http
      .expectOne((r) => r.url === `${API_URL}/medicamentos` && r.params.get('disponibles') === 'true')
      .flush(medicamentos);
    fixture.detectChanges();
  }

  function enviar() {
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  function botonGuardar(): HTMLButtonElement {
    return fixture.nativeElement.querySelector('button[type="submit"]');
  }

  it('sin medicamentos con unidades muestra el aviso y no deja registrar', () => {
    cargar([]);

    expect(fixture.nativeElement.textContent).toContain('No hay medicamentos con unidades disponibles.');
    expect(fixture.nativeElement.querySelector('#medicamento')).toBeNull();
    expect(botonGuardar().disabled).toBeTrue();
  });

  it('preselecciona la mascota de la URL', () => {
    cargar([amoxicilina]);

    expect(component.tratamientoForm.controls.mascota.value).toBe(1);
  });

  it('la cantidad máxima son las unidades del medicamento elegido', () => {
    cargar([amoxicilina, meloxicam]);
    const cantidad = component.tratamientoForm.controls.cantidad;

    component.tratamientoForm.controls.medicamento.setValue(amoxicilina);
    cantidad.setValue(4);
    expect(cantidad.hasError('max')).toBeTrue();
    cantidad.setValue(3);
    expect(cantidad.valid).toBeTrue();

    component.tratamientoForm.controls.medicamento.setValue(meloxicam);
    cantidad.setValue(8);
    expect(cantidad.valid).toBeTrue();
  });

  it('al guardar envía el tratamiento y vuelve al detalle de la mascota', () => {
    cargar([amoxicilina]);
    const navigate = spyOn(TestBed.inject(Router), 'navigate');
    component.tratamientoForm.controls.medicamento.setValue(amoxicilina);
    component.tratamientoForm.controls.cantidad.setValue(2);

    enviar();

    const post = http.expectOne(`${API_URL}/tratamientos`);
    expect(post.request.method).toBe('POST');
    expect(post.request.body).toEqual({ mascotaId: 1, veterinarioId: 1, medicamentoId: 10, cantidad: 2 });
    post.flush({ id: 99, cantidad: 2, medicamentoNombre: 'Amoxicilina', mascotaNombre: 'Max' });

    expect(navigate).toHaveBeenCalledWith(['/vet/800000001/mascota', 1], jasmine.objectContaining({ state: jasmine.any(Object) }));
  });

  it('un 409 muestra el mensaje de la API y recarga el stock del medicamento elegido', () => {
    cargar([amoxicilina, meloxicam]);
    const navigate = spyOn(TestBed.inject(Router), 'navigate');
    component.tratamientoForm.controls.medicamento.setValue(amoxicilina);
    component.tratamientoForm.controls.cantidad.setValue(3);

    enviar();
    http
      .expectOne(`${API_URL}/tratamientos`)
      .flush({ mensaje: 'Solo quedan 1 unidades de Amoxicilina' }, { status: 409, statusText: 'Conflict' });
    // Otro veterinario se llevó 2 unidades: la lista se vuelve a pedir
    http
      .expectOne((r) => r.url === `${API_URL}/medicamentos` && r.params.get('disponibles') === 'true')
      .flush([{ ...amoxicilina, unidadesDisponibles: 1 }, meloxicam]);
    fixture.detectChanges();

    expect(navigate).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Solo quedan 1 unidades de Amoxicilina');
    expect(component.tratamientoForm.controls.medicamento.value?.unidadesDisponibles).toBe(1);
    const cantidad = component.tratamientoForm.controls.cantidad;
    cantidad.setValue(2);
    expect(cantidad.hasError('max')).toBeTrue();
  });

  it('si tras el 409 el medicamento ya no tiene unidades se quita de la selección', () => {
    cargar([amoxicilina, meloxicam]);
    component.tratamientoForm.controls.medicamento.setValue(amoxicilina);

    enviar();
    http
      .expectOne(`${API_URL}/tratamientos`)
      .flush({ mensaje: 'No quedan unidades de Amoxicilina' }, { status: 409, statusText: 'Conflict' });
    http.expectOne((r) => r.url === `${API_URL}/medicamentos`).flush([meloxicam]);
    fixture.detectChanges();

    expect(component.medicamentos).toEqual([meloxicam]);
    expect(component.tratamientoForm.controls.medicamento.value).toBeNull();
  });

  it('otros errores no recargan los medicamentos', () => {
    cargar([amoxicilina]);
    component.tratamientoForm.controls.medicamento.setValue(amoxicilina);

    enviar();
    http
      .expectOne(`${API_URL}/tratamientos`)
      .flush({ mensaje: 'No existe una mascota con el id 1' }, { status: 404, statusText: 'Not Found' });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No existe una mascota con el id 1');
    // http.verify() en afterEach comprueba que no se pidió otra vez /medicamentos
  });
});
