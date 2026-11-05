import { Component } from '@angular/core';
import { UserSearchComponent } from './pages/user-search/user-search.component';

@Component({
  selector: 'app-root',
  imports: [UserSearchComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {}
