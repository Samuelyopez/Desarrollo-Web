import { defineConfig } from 'cypress';

// Pruebas de extremo a extremo (e2e): Cypress abre el navegador contra Angular (4200) y la API (8090)
// reales. Hay que tener los dos encendidos antes de correr `npm run e2e`.
export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    specPattern: 'cypress/e2e/**/*.cy.ts',
    supportFile: 'cypress/support/e2e.ts',
    viewportWidth: 1280,
    viewportHeight: 800,
    video: false,
    // La API tarda un poco en responder la primera vez (H2 + Hibernate): se da más margen que el default (4 s)
    defaultCommandTimeout: 10000,
  },
  env: {
    // Misma URL que src/app/api.config.ts. Se usa para leer el estado antes y después (cy.request)
    apiUrl: 'http://localhost:8090/api',
  },
});
