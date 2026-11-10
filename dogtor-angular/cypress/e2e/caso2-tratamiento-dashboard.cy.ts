// Caso 2 (e2e): el veterinario suministra un medicamento a una mascota.
// Busca la mascota, le da un tratamiento y guarda. Para verificar, la busca otra vez, entra al detalle y ve el
// tratamiento guardado. Después entra el administrador y revisa en el dashboard que la cantidad de medicamentos
// suministrados y las ganancias sean correctas.

import { Dashboard } from '../../src/app/models/dashboard.model';
import { Medicamento } from '../../src/app/models/medicamento.model';
import { hoyDdMmYyyy, kpi, soloDigitos } from '../support/utils';

const API = Cypress.env('apiUrl') as string;
const VETERINARIO = { correo: 'veterinario@dogtor.com', password: 'vet123', cedula: '800000001', nombre: 'Andrés Felipe Mora' };
const ADMIN = { correo: 'admin@dogtor.com', password: 'admin123' };
const MASCOTA = 'Rocky'; // de los datos de prueba (María López), activa
const CANTIDAD = 2;

describe('Caso 2: dar un tratamiento y verificar el dashboard', () => {
  // Estado ANTES de la prueba, leído de la API, para calcular lo que debe mostrar el dashboard DESPUÉS
  let antes: Dashboard;
  let medicamento: Medicamento;
  let tratamientosAntes = 0;

  before(() => {
    cy.request<Dashboard>(`${API}/dashboard`).then((resp) => {
      antes = resp.body;
    });
    cy.request<Medicamento[]>(`${API}/medicamentos?disponibles=true`).then((resp) => {
      const conUnidades = resp.body.find((m) => m.unidadesDisponibles >= CANTIDAD);
      expect(conUnidades, `un medicamento con al menos ${CANTIDAD} unidades`).to.exist;
      medicamento = conUnidades!;
    });
  });

  it('el veterinario busca la mascota, le da un tratamiento y lo ve guardado en el detalle', () => {
    cy.login(VETERINARIO.correo, VETERINARIO.password);
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}`);

    // --- Busca la mascota en la barra de búsqueda y la selecciona ---
    cy.contains('a.nav-link', 'Mascotas').click();
    cy.get('#buscarMascota').type(MASCOTA);
    cy.contains('app-mascota-table tr', MASCOTA).contains('button', 'Ver').click();
    cy.location('pathname').should('match', new RegExp(`^/vet/${VETERINARIO.cedula}/mascota/\\d+$`));
    cy.contains('h1', MASCOTA);
    cy.contains('h2', 'Historial de tratamientos')
      .invoke('text')
      .then((texto) => {
        tratamientosAntes = soloDigitos(texto);
      });

    // --- Le da un nuevo tratamiento ---
    cy.contains('a', 'Dar tratamiento').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/tratamiento/new`);
    cy.get('#veterinario').should('have.value', VETERINARIO.nombre);
    cy.get('#fecha').should('have.value', hoyDdMmYyyy());
    // La mascota viene preseleccionada desde su detalle (?mascota=id)
    cy.get('#mascota option:selected').should('contain.text', MASCOTA);
    // Se elige el medicamento por su nombre (el texto de la opción trae también precio y unidades)
    cy.get('#medicamento option')
      .contains(medicamento.nombre)
      .then((opcion) => {
        cy.get('#medicamento').select(opcion.val() as string);
      });
    cy.get('#cantidad').clear().type(String(CANTIDAD));
    cy.contains('button', 'Registrar tratamiento').click();

    // Vuelve al detalle con el mensaje de confirmación
    cy.location('pathname').should('match', /\/mascota\/\d+$/);
    cy.get('.alert-success').should('contain.text', `Se registró ${medicamento.nombre} x${CANTIDAD} para ${MASCOTA}.`);

    // --- Verifica: busca la mascota otra vez, entra al detalle y el tratamiento está guardado ---
    cy.contains('a.nav-link', 'Mascotas').click();
    cy.get('#buscarMascota').type(MASCOTA);
    cy.contains('app-mascota-table tr', MASCOTA).contains('button', 'Ver').click();
    cy.contains('h1', MASCOTA);
    // Dentro de cy.then para leer tratamientosAntes cuando ya se asignó (los comandos de Cypress se encolan)
    cy.then(() => {
      cy.contains('h2', `Historial de tratamientos (${tratamientosAntes + 1})`);
    });
    // El historial va del más reciente al más antiguo: el nuevo es la primera fila
    cy.get('app-tratamiento-list tbody tr')
      .first()
      .within(() => {
        cy.get('td').eq(0).should('have.text', hoyDdMmYyyy());
        cy.get('td').eq(1).should('have.text', medicamento.nombre);
        cy.get('td').eq(2).should('contain.text', VETERINARIO.nombre);
        cy.get('td').eq(3).should('have.text', String(CANTIDAD));
        cy.get('td')
          .eq(5)
          .invoke('text')
          .then((total) => expect(soloDigitos(total)).to.eq(Math.round(medicamento.precioVenta * CANTIDAD)));
      });
  });

  it('el administrador ve en el dashboard los medicamentos suministrados y las ganancias correctas', () => {
    cy.login(ADMIN.correo, ADMIN.password);
    cy.location('pathname').should('eq', '/admin');
    cy.contains('a.nav-link', 'Dashboard').click();
    cy.location('pathname').should('eq', '/admin/dashboard');
    cy.contains('h1', 'Dashboard');

    // Lo esperado: el dashboard de antes + el tratamiento que se acaba de dar
    const tratamientosEsperados = antes.tratamientosUltimoMes + 1;
    const unidadesEsperadas = antes.unidadesUltimoMes + CANTIDAD;
    const ventasEsperadas = antes.ventasTotales + medicamento.precioVenta * CANTIDAD;

    // 1. La API ya refleja el tratamiento (ventas exactas; la ganancia sube sin superar la venta,
    //    el precio de compra no se expone al front)
    cy.request<Dashboard>(`${API}/dashboard`).then((resp) => {
      const despues = resp.body;
      expect(despues.tratamientosUltimoMes, 'tratamientos del mes').to.eq(tratamientosEsperados);
      expect(despues.unidadesUltimoMes, 'unidades del mes').to.eq(unidadesEsperadas);
      expect(despues.ventasTotales, 'ventas totales').to.be.closeTo(ventasEsperadas, 0.01);
      const ganancia = despues.gananciasTotales - antes.gananciasTotales;
      expect(ganancia, 'ganancia del tratamiento').to.be.greaterThan(0);
      expect(ganancia, 'ganancia del tratamiento').to.be.at.most(medicamento.precioVenta * CANTIDAD + 0.01);

      // 2. Y el dashboard muestra exactamente esos números
      kpi('Tratamientos del mes')
        .find('.valor')
        .invoke('text')
        .then((texto) => expect(soloDigitos(texto)).to.eq(tratamientosEsperados));
      kpi('Tratamientos del mes')
        .find('.detalle')
        .should('contain.text', 'unidades suministradas')
        .invoke('text')
        .then((texto) => expect(soloDigitos(texto)).to.eq(unidadesEsperadas));
      kpi('Ventas totales')
        .find('.valor')
        .invoke('text')
        .then((texto) => expect(soloDigitos(texto)).to.eq(Math.round(ventasEsperadas)));
      kpi('Ganancias totales')
        .find('.valor')
        .invoke('text')
        .then((texto) => expect(soloDigitos(texto)).to.eq(Math.round(despues.gananciasTotales)));

      // 3. En "Tratamientos por medicamento" el medicamento suministrado suma las unidades de hoy
      const filaAntes = antes.tratamientosPorMedicamento.find((f) => f.medicamento === medicamento.nombre);
      const unidadesMedicamento = (filaAntes?.unidades ?? 0) + CANTIDAD;
      cy.contains('h2', 'Tratamientos por medicamento')
        .closest('.card-body')
        .contains('tbody tr', medicamento.nombre)
        .find('td')
        .eq(2)
        .invoke('text')
        .then((texto) => expect(soloDigitos(texto)).to.eq(unidadesMedicamento));
    });
  });
});
