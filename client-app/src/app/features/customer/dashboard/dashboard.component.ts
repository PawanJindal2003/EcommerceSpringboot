import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({ selector: 'app-customer-dashboard', templateUrl: './dashboard.component.html' })
export class CustomerDashboardComponent {
  constructor(public auth: AuthService) {}
}
