// Comandos propios de Cypress para DogTor

declare global {
  namespace Cypress {
    interface Chainable {
      // Va a /login, escribe el correo y la contraseña y pulsa "Ingresar".
      // No valida el resultado: cada prueba revisa si entró o si vio el error
      login(correo: string, password: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('login', (correo: string, password: string) => {
  cy.visit('/login');
  cy.get('#correo').clear().type(correo);
  cy.get('#password').clear().type(password);
  cy.contains('button', 'Ingresar').click();
});

export {};
