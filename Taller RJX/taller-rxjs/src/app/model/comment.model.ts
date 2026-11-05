// Interfaces generadas a partir del JSON que devuelve https://dummyjson.com/comments

export interface Comment {
  id: number;
  body: string;
  postId: number;
  likes: number;
  user: CommentUser;
}

// Usuario que realizó el comentario
export interface CommentUser {
  id: number;
  username: string;
  fullName: string;
}

// Respuesta de /comments/post/{postId}
export interface CommentsResponse {
  comments: Comment[];
  total: number;
  skip: number;
  limit: number;
}
