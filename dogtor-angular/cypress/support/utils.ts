// Ayudas para comparar lo que pinta Angular con los números de la API

// "$ 1.234.567" o "12 unidades" -> 1234567 / 12. Los pipes es-CO usan punto de miles y símbolo de moneda,
// así que se comparan solo los dígitos (los KPI se muestran sin decimales: formato '1.0-0')
export function soloDigitos(texto: string): number {
  return Number(texto.replace(/\D/g, ''));
}

// Fecha de hoy como la muestra el pipe date 'dd/MM/yyyy'
export function hoyDdMmYyyy(): string {
  const hoy = new Date();
  const dd = String(hoy.getDate()).padStart(2, '0');
  const mm = String(hoy.getMonth() + 1).padStart(2, '0');
  return `${dd}/${mm}/${hoy.getFullYear()}`;
}

// Tarjeta KPI del dashboard por su título (app-kpi-card)
export function kpi(titulo: string) {
  return cy.contains('app-kpi-card', titulo);
}
