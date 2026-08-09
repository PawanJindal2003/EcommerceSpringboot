import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({ selector: 'app-seller-dashboard', templateUrl: './dashboard.component.html' })
export class SellerDashboardComponent {
  constructor(public auth: AuthService) {}
}
