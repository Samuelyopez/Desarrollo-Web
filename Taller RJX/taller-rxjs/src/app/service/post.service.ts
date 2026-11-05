import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Post, PostsResponse } from '../model/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  ROOT_URL = 'https://dummyjson.com';
  private http = inject(HttpClient);

  // Trae todos los posts relacionados al usuario con el userId dado
  getPostsByUser(userId: number): Observable<Post[]> {
    return this.http
      .get<PostsResponse>(`${this.ROOT_URL}/posts/user/${userId}`)
      .pipe(map((response) => response.posts));
  }
}
