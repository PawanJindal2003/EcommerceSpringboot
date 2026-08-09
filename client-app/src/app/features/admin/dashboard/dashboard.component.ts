import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({ selector: 'app-admin-dashboard', templateUrl: './dashboard.component.html' })
export class AdminDashboardComponent {
  constructor(public auth: AuthService) {}
}
