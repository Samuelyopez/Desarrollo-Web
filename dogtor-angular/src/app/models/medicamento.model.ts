// Entidad Medicamento (tabla medicamentos): se carga desde el Excel al iniciar la API
export interface Medicamento {
  id: number;
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
  unidadesVendidas: number;
}
