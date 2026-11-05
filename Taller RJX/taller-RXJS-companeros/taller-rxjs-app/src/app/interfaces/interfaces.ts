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
  password: string;
  birthDate: string;
  image: string;
  bloodGroup: string;
  height: number;
  weight: number;
  eyeColor: string;
  hair: { color: string; type: string };
  ip: string;
  address: Address;
  university: string;
  company: Company;
  role: string;
}

export interface Address {
  address: string;
  city: string;
  state: string;
  stateCode: string;
  postalCode: string;
  coordinates: { lat: number; lng: number };
  country: string;
}

export interface Company {
  department: string;
  name: string;
  title: string;
  address: Address;
}

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

export interface Comment {
  id: number;
  body: string;
  postId: number;
  likes: number;
  user: CommentUser;
}

export interface CommentUser {
  id: number;
  username: string;
  fullName: string;
}

export interface UserResponse {
  users: User[];
  total: number;
  skip: number;
  limit: number;
}

export interface PostResponse {
  posts: Post[];
  total: number;
  skip: number;
  limit: number;
}

export interface CommentResponse {
  comments: Comment[];
  total: number;
  skip: number;
  limit: number;
}

export interface PostWithComments extends Post {
  comments: Comment[];
}
