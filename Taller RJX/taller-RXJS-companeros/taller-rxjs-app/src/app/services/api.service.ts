import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { User, Post, Comment, UserResponse, PostResponse, CommentResponse } from '../interfaces/interfaces';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'https://dummyjson.com';

  constructor(private http: HttpClient) {}

  searchUserByUsername(username: string): Observable<User | null> {
    return this.http.get<UserResponse>(`${this.baseUrl}/users/filter?key=username&value=${username}`)
      .pipe(
        map(response => response.users.length > 0 ? response.users[0] : null)
      );
  }

  getPostsByUserId(userId: number): Observable<Post[]> {
    return this.http.get<PostResponse>(`${this.baseUrl}/posts/user/${userId}`)
      .pipe(
        map(response => response.posts)
      );
  }

  getCommentsByPostId(postId: number): Observable<Comment[]> {
    return this.http.get<CommentResponse>(`${this.baseUrl}/comments/post/${postId}`)
      .pipe(
        map(response => response.comments)
      );
  }
}
