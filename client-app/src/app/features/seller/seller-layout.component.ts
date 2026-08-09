import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({ selector: 'app-seller-layout', template: `
  <nav class="navbar">
    <div class="nav-brand"><a routerLink="/seller">ECommerce Seller</a></div>
    <div class="nav-links">
      <a routerLink="/seller" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Dashboard</a>
      <a routerLink="/seller/products" routerLinkActive="active">Products</a>
      <a routerLink="/seller/profile" routerLinkActive="active">Profile</a>
      <button class="btn btn-sm btn-outline" (click)="logout()">Logout</button>
    </div>
  </nav>
  <main class="main-content"><router-outlet></router-outlet></main>
` })
export class SellerLayoutComponent {
  constructor(private auth: AuthService, private router: Router) {}
  logout() { this.auth.logout().subscribe(() => this.router.navigate(['/auth/login'])); }
}
