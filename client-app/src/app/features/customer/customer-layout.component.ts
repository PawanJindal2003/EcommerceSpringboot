import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({ selector: 'app-customer-layout', template: `
  <nav class="navbar">
    <div class="nav-brand"><a routerLink="/shop">ECommerce Shop</a></div>
    <div class="nav-links">
      <a routerLink="/shop" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Home</a>
      <a routerLink="/shop/catalog" routerLinkActive="active">Catalog</a>
      <a routerLink="/shop/profile" routerLinkActive="active">Profile</a>
      <a routerLink="/shop/addresses" routerLinkActive="active">Addresses</a>
      <button class="btn btn-sm btn-outline" (click)="logout()">Logout</button>
    </div>
  </nav>
  <main class="main-content"><router-outlet></router-outlet></main>
` })
export class CustomerLayoutComponent {
  constructor(private auth: AuthService, private router: Router) {}
  logout() { this.auth.logout().subscribe(() => this.router.navigate(['/auth/login'])); }
}
