import { Component, Input } from '@angular/core';
import { PostWithComments } from '../../model/post.model';

@Component({
  selector: 'app-user-posts',
  imports: [],
  templateUrl: './user-posts.component.html',
  styleUrl: './user-posts.component.css'
})
export class UserPostsComponent {
  // El componente principal hace las consultas y nos pasa los posts con sus comentarios
  @Input() posts: PostWithComments[] = [];
}
