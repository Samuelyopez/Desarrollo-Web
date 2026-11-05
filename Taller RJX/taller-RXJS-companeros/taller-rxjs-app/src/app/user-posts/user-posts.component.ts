import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostWithComments } from '../interfaces/interfaces';

@Component({
  selector: 'app-user-posts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="mt-4">
      <h4 class="mb-3"><i class="bi bi-file-earmark-text"></i> Posts ({{ posts.length }})</h4>

      @if (posts.length === 0) {
        <div class="alert alert-info">
          <i class="bi bi-info-circle"></i> Este usuario no tiene posts.
        </div>
      }

      @for (post of posts; track post.id) {
        <div class="card mb-3 shadow-sm">
          <div class="card-body">
            <h5 class="card-title">{{ post.title }}</h5>
            <p class="card-text">{{ post.body }}</p>

            <!-- Tags -->
            <div class="mb-2">
              @for (tag of post.tags; track tag) {
                <span class="badge bg-secondary me-1">{{ tag }}</span>
              }
            </div>

            <!-- Reacciones -->
            <div class="d-flex gap-3 mb-3 text-muted">
              <span><i class="bi bi-hand-thumbs-up-fill text-primary"></i> {{ post.reactions.likes }}</span>
              <span><i class="bi bi-hand-thumbs-down-fill text-danger"></i> {{ post.reactions.dislikes }}</span>
              <span><i class="bi bi-eye-fill"></i> {{ post.views }}</span>
            </div>

            <!-- Comentarios -->
            @if (post.comments && post.comments.length > 0) {
              <hr>
              <h6 class="text-muted"><i class="bi bi-chat-dots"></i> Comentarios ({{ post.comments.length }})</h6>
              @for (comment of post.comments; track comment.id) {
                <div class="d-flex align-items-start mb-2 ps-3 border-start border-2">
                  <div>
                    <strong>{{ comment.user.fullName }}</strong>
                    <span class="text-muted ms-2">&#64;{{ comment.user.username }}</span>
                    <p class="mb-1">{{ comment.body }}</p>
                    <small class="text-muted"><i class="bi bi-heart-fill text-danger"></i> {{ comment.likes }}</small>
                  </div>
                </div>
              }
            } @else {
              <hr>
              <p class="text-muted"><i class="bi bi-chat"></i> Sin comentarios</p>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class UserPostsComponent {
  @Input() posts: PostWithComments[] = [];
}
