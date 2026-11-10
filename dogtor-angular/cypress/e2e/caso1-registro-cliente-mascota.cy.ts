// Caso 1 (e2e): llega un cliente nuevo con su mascota.
// El veterinario se equivoca al ingresar y entra al segundo intento; registra al dueño (se equivoca en un campo,
// corrige y guarda) y luego registra la mascota asociada al dueño. El dueño entra a su portal con su cédula
// en la URL y ve que los datos de la mascota son correctos.

const VETERINARIO = { correo: 'veterinario@dogtor.com', password: 'vet123', cedula: '800000001', nombre: 'Andrés Felipe Mora' };

// Datos únicos por ejecución: la API rechaza (409) una cédula o un correo repetidos
const sufijo = Date.now().toString().slice(-8);
const DUENO = {
  cedula: `12${sufijo}`, // 10 dígitos
  nombre: 'Laura Prueba',
  celularMalo: '12345', // no cumple el patrón (10 dígitos y empezar por 3)
  celular: `3${sufijo}5`,
  correo: `laura.${sufijo}@correo.com`,
  password: 'cliente123',
};
const MASCOTA = { nombre: 'Kira', raza: 'Beagle', edad: '3', peso: '9.5', enfermedad: 'Otitis' };

describe('Caso 1: registro de un cliente nuevo y su mascota', () => {
  it('el veterinario ingresa (al segundo intento), registra al dueño (al segundo intento) y a la mascota', () => {
    // --- Login: primer intento con la contraseña mal ---
    cy.login(VETERINARIO.correo, 'contrasena-mala');
    cy.get('.alert-danger').should('be.visible').and('contain.text', 'Correo o contraseña incorrectos');
    cy.location('pathname').should('eq', '/login');

    // --- Segundo intento: entra a su portal ---
    cy.get('#password').clear().type(VETERINARIO.password);
    cy.contains('button', 'Ingresar').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}`);
    cy.contains('h1', `Hola, ${VETERINARIO.nombre}`);

    // --- Registro de clientes: se equivoca en el celular ---
    cy.contains('a.nav-link', 'Dueños').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/duenos`);
    cy.contains('a', 'Nuevo Dueño').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/dueno/new`);

    cy.get('#cedula').type(DUENO.cedula);
    cy.get('#nombre').type(DUENO.nombre);
    cy.get('#celular').type(DUENO.celularMalo);
    cy.get('#correo').type(DUENO.correo);
    cy.get('#password').type(DUENO.password);
    cy.get('form button[type="submit"]').click();

    // El formulario no se envía: muestra el error del campo y sigue en la misma página
    cy.get('#celular').should('have.class', 'is-invalid');
    cy.contains('.invalid-feedback', 'El celular debe tener 10 dígitos y empezar por 3.');
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/dueno/new`);

    // --- Corrige e intenta de nuevo: el cliente queda registrado ---
    cy.get('#celular').clear().type(DUENO.celular);
    cy.get('form button[type="submit"]').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/duenos`);
    cy.get('.alert-success').should('contain.text', `Se registró a ${DUENO.nombre}.`);
    cy.contains('app-dueno-table tr', DUENO.cedula).should('contain.text', DUENO.nombre);

    // --- Mascotas: registra la mascota y la asocia al dueño. Todo sale bien a la primera ---
    cy.contains('a.nav-link', 'Mascotas').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/mascotas`);
    cy.contains('a', 'Nueva Mascota').click();
    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/mascota/new`);

    cy.get('#nombre').type(MASCOTA.nombre);
    cy.get('#raza').type(MASCOTA.raza);
    cy.get('#edad').type(MASCOTA.edad);
    cy.get('#peso').type(MASCOTA.peso);
    cy.get('#enfermedad').type(MASCOTA.enfermedad);
    // La lista de dueños llega de la API: se espera a que tenga opciones y se elige al dueño recién creado
    cy.get('#dueno option').should('have.length.greaterThan', 1);
    cy.get('#dueno').select(`${DUENO.nombre} (C.C. ${DUENO.cedula})`);
    cy.get('form button[type="submit"]').click();

    cy.location('pathname').should('eq', `/vet/${VETERINARIO.cedula}/mascotas`);
    cy.get('.alert-success').should('contain.text', `Se registró a ${MASCOTA.nombre} (dueño: ${DUENO.nombre}).`);
    cy.contains('app-mascota-table tr', DUENO.nombre).should('contain.text', MASCOTA.nombre);
  });

  it('el dueño ingresa al portal de clientes con su cédula y ve los datos de su mascota', () => {
    // El login lo lleva a su portal: la cédula va en la URL (/cliente/<cédula>)
    cy.login(DUENO.correo, DUENO.password);
    cy.location('pathname').should('eq', `/cliente/${DUENO.cedula}`);
    cy.contains('h1', `Hola, ${DUENO.nombre}`);

    cy.contains('a.nav-link', 'Mis mascotas').click();
    cy.location('pathname').should('eq', `/cliente/${DUENO.cedula}/mascotas`);
    cy.contains('app-mascota-card', MASCOTA.nombre).click();
    cy.location('pathname').should('match', new RegExp(`^/cliente/${DUENO.cedula}/mascota/\\d+$`));

    // Datos de la mascota tal como los registró el veterinario
    cy.contains('h1', MASCOTA.nombre);
    cy.contains('.badge', 'En la clínica');
    cy.contains('tr', 'Raza').should('contain.text', MASCOTA.raza);
    cy.contains('tr', 'Edad').should('contain.text', `${MASCOTA.edad} año(s)`);
    cy.contains('tr', 'Peso').should('contain.text', `${MASCOTA.peso} kg`);
    cy.contains('tr', 'Enfermedad').should('contain.text', MASCOTA.enfermedad);
    cy.contains('h2', 'Historial de tratamientos (0)');
    cy.contains('Esta mascota aún no tiene tratamientos registrados.');
  });
});
