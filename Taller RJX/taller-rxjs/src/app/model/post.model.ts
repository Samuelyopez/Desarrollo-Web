import { Comment } from './comment.model';

// Interfaces generadas a partir del JSON que devuelve https://dummyjson.com/posts

export interface Post {
  id: number;
  title: string;
  body: string;
  tags: string[];
  reactions: Reactions;
  views: number;
  userId: number;
}

export interface Reactions {
  likes: number;
  dislikes: number;
}

// Respuesta de /posts/user/{userId}
export interface PostsResponse {
  posts: Post[];
  total: number;
  skip: number;
  limit: number;
}

// Post junto con sus comentarios, listo para mostrarse en pantalla
export interface PostWithComments extends Post {
  comments: Comment[];
}
