// Entidad Medicamento (tabla medicamentos): se carga desde el Excel al iniciar la API.
// El precio de compra no llega al front (es dato interno de ganancias)
export interface Medicamento {
  id: number;
  nombre: string;
  precioVenta: number;
  unidadesDisponibles: number;
  unidadesVendidas: number;
}
