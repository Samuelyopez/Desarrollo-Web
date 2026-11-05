import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Comment, CommentsResponse } from '../model/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  ROOT_URL = 'https://dummyjson.com';
  private http = inject(HttpClient);

  // Trae los comentarios relacionados al post con el postId dado
  getCommentsByPost(postId: number): Observable<Comment[]> {
    return this.http
      .get<CommentsResponse>(`${this.ROOT_URL}/comments/post/${postId}`)
      .pipe(map((response) => response.comments));
  }
}
