import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { User, UsersResponse } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  ROOT_URL = 'https://dummyjson.com';
  private http = inject(HttpClient);

  // Busca un usuario por su username.
  // La API devuelve un arreglo: tiene 1 elemento si el usuario existe y 0 si no existe.
  searchUser(username: string): Observable<User[]> {
    return this.http
      .get<UsersResponse>(`${this.ROOT_URL}/users/filter`, {
        params: { key: 'username', value: username }
      })
      .pipe(map((response) => response.users));
  }
}
