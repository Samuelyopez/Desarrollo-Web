import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ApiService } from './services/api.service';
import { User, PostWithComments } from './interfaces/interfaces';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { UserPostsComponent } from './user-posts/user-posts.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, UserProfileComponent, UserPostsComponent],
  template: `
    <!-- Sección 1: Barra de búsqueda -->
    <div class="search-container">
      <div class="container">
        <h1 class="text-center mb-4"><i class="bi bi-search"></i> Búsqueda de Perfil</h1>
        <div class="row justify-content-center">
          <div class="col-md-8">
            <div class="input-group">
              <input
                type="text"
                class="form-control"
                placeholder="Ingresa un username (ej: emilys, michaelw, sophiab)"
                [(ngModel)]="username"
                (keyup.enter)="searchUser()">
              <button class="btn btn-light" (click)="searchUser()" [disabled]="loading">
                @if (loading) {
                  <span class="spinner-border spinner-border-sm me-1"></span>
                  Buscando...
                } @else {
                  <i class="bi bi-search"></i> Buscar
                }
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container pb-5">
      <!-- Mensaje de error si el usuario no existe -->
      @if (errorMessage) {
        <div class="alert alert-warning d-flex align-items-center" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2 fs-4"></i>
          <div>{{ errorMessage }}</div>
        </div>
      }

      <!-- Sección 2: Datos del usuario -->
      @if (user) {
        <app-user-profile [user]="user"></app-user-profile>
      }

      <!-- Sección 3: Posts del usuario -->
      @if (user && postsWithComments) {
        <app-user-posts [posts]="postsWithComments"></app-user-posts>
      }
    </div>
  `
})
export class AppComponent {
  username: string = '';
  user: User | null = null;
  postsWithComments: PostWithComments[] | null = null;
  errorMessage: string = '';
  loading: boolean = false;

  constructor(private apiService: ApiService) {}

  searchUser(): void {
    const trimmedUsername = this.username.trim();
    if (!trimmedUsername) {
      this.errorMessage = 'Por favor ingresa un nombre de usuario.';
      return;
    }

    // Limpiar estado anterior
    this.user = null;
    this.postsWithComments = null;
    this.errorMessage = '';
    this.loading = true;

    // Paso 1: Buscar usuario por username usando RxJS
    this.apiService.searchUserByUsername(trimmedUsername)
      .pipe(
        // Paso 2: Si el usuario existe, obtener sus posts
        switchMap(user => {
          if (!user) {
            throw new Error('USER_NOT_FOUND');
          }
          this.user = user;
          return this.apiService.getPostsByUserId(user.id);
        }),
        // Paso 3: Para cada post, obtener sus comentarios
        switchMap(posts => {
          if (posts.length === 0) {
            return [[]];
          }
          const commentRequests = posts.map(post =>
            this.apiService.getCommentsByPostId(post.id)
          );
          return forkJoin(commentRequests).pipe(
            switchMap(commentsArrays => {
              const postsWithComments: PostWithComments[] = posts.map((post, index) => ({
                ...post,
                comments: commentsArrays[index]
              }));
              return [postsWithComments];
            })
          );
        })
      )
      .subscribe({
        next: (postsWithComments) => {
          this.postsWithComments = postsWithComments;
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          if (error.message === 'USER_NOT_FOUND') {
            this.errorMessage = `El usuario "${trimmedUsername}" no existe. Intenta con otro username.`;
          } else {
            this.errorMessage = 'Ocurrió un error al realizar la búsqueda. Intenta de nuevo.';
          }
        }
      });
  }
}
