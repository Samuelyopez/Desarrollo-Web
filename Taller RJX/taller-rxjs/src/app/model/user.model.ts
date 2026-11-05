// Interfaces generadas a partir del JSON que devuelve https://dummyjson.com/users

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  maidenName: string;
  age: number;
  gender: string;
  email: string;
  phone: string;
  username: string;
  birthDate: string;
  image: string;
  bloodGroup: string;
  height: number;
  weight: number;
  eyeColor: string;
  hair: Hair;
  address: Address;
  university: string;
  company: Company;
  role: string;
}

export interface Hair {
  color: string;
  type: string;
}

export interface Address {
  address: string;
  city: string;
  state: string;
  stateCode: string;
  postalCode: string;
  coordinates: Coordinates;
  country: string;
}

export interface Coordinates {
  lat: number;
  lng: number;
}

export interface Company {
  department: string;
  name: string;
  title: string;
  address: Address;
}

// Respuesta de /users/filter?key=username&value=...
// La API devuelve un ARREGLO de usuarios dentro de "users"
export interface UsersResponse {
  users: User[];
  total: number;
  skip: number;
  limit: number;
}
