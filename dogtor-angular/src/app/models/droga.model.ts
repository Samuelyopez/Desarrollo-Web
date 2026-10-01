// Entidad Droga (tabla drogas): medicamentos que se pueden recetar
export interface Droga {
  id: number;
  nombre: string;
  descripcion?: string;
  dosisRecomendada?: string;
}
