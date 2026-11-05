import { Component, Input } from '@angular/core';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-user-info',
  imports: [],
  templateUrl: './user-info.component.html',
  styleUrl: './user-info.component.css'
})
export class UserInfoComponent {
  // El componente principal hace la consulta y nos pasa el usuario
  @Input({ required: true }) user!: User;
}
