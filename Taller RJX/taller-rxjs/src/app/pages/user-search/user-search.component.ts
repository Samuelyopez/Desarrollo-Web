import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormSubmittedEvent,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { catchError, filter, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { User } from '../../model/user.model';
import { Post, PostWithComments } from '../../model/post.model';
import { UserService } from '../../service/user.service';
import { PostService } from '../../service/post.service';
import { CommentService } from '../../service/comment.service';
import { UserInfoComponent } from '../../components/user-info/user-info.component';
import { UserPostsComponent } from '../../components/user-posts/user-posts.component';

@Component({
  selector: 'app-user-search',
  imports: [ReactiveFormsModule, UserInfoComponent, UserPostsComponent],
  templateUrl: './user-search.component.html',
  styleUrl: './user-search.component.css'
})
export class UserSearchComponent {

  private userService = inject(UserService);
  private postService = inject(PostService);
  private commentService = inject(CommentService);

  userForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  user: User | null = null;
  posts: PostWithComments[] = [];

  searchedUsername = '';
  loading = false;
  notFound = false;
  errorMessage = '';

  constructor() {
    // Reaccionamos a los eventos del formulario (RXJS).
    // Un único subscribe: las peticiones se encadenan con switchMap, nunca con subscribes anidados.
    this.userForm.events
      .pipe(
        // 1. Solo dejamos pasar el evento de submit y cuando el formulario es válido
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => this.userForm.valid),

        // 2. Buscamos el usuario por su username.
        //    switchMap cancela la búsqueda anterior si el usuario busca otro antes de que llegue la respuesta.
        switchMap(() => {
          const username = this.userForm.value.username!.trim();
          this.startSearch(username);

          return this.userService
            .searchUser(username)
            .pipe(catchError(() => this.handleError<User[]>([])));
        }),

        // 3. Con el usuario encontrado buscamos sus posts
        switchMap((users) => {
          // La API devuelve un arreglo: si viene vacío el usuario no existe
          if (users.length === 0) {
            this.notFound = !this.errorMessage;
            return of([] as Post[]);
          }

          this.user = users[0];
          return this.postService
            .getPostsByUser(this.user.id)
            .pipe(catchError(() => this.handleError<Post[]>([])));
        }),

        // 4. Para cada post buscamos sus comentarios.
        //    forkJoin lanza las peticiones en paralelo y emite cuando TODAS terminan,
        //    así cada post queda con sus propios comentarios.
        switchMap((posts) => {
          if (posts.length === 0) {
            return of([] as PostWithComments[]);
          }

          return forkJoin(posts.map((post) => this.getPostWithComments(post)))
            .pipe(catchError(() => this.handleError<PostWithComments[]>([])));
        })
      )
      .subscribe((posts) => {
        this.posts = posts;
        this.loading = false;
      });
  }

  // Une un post con sus comentarios
  private getPostWithComments(post: Post): Observable<PostWithComments> {
    return this.commentService
      .getCommentsByPost(post.id)
      .pipe(map((comments) => ({ ...post, comments })));
  }

  // Limpia la información de la búsqueda anterior para no mostrar datos de otro usuario
  private startSearch(username: string): void {
    this.searchedUsername = username;
    this.user = null;
    this.posts = [];
    this.notFound = false;
    this.errorMessage = '';
    this.loading = true;
  }

  // Si una petición falla, informamos al usuario y seguimos escuchando nuevas búsquedas
  private handleError<T>(fallback: T): Observable<T> {
    this.user = null;
    this.errorMessage = 'Ocurrió un error consultando la API. Intenta de nuevo.';
    return of(fallback);
  }
}
