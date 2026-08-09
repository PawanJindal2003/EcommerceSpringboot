import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({ selector: 'app-admin-layout', template: `
  <nav class="navbar">
    <div class="nav-brand"><a routerLink="/admin">ECommerce Admin</a></div>
    <div class="nav-links">
      <a routerLink="/admin" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Dashboard</a>
      <a routerLink="/admin/customers" routerLinkActive="active">Customers</a>
      <a routerLink="/admin/sellers" routerLinkActive="active">Sellers</a>
      <a routerLink="/admin/categories" routerLinkActive="active">Categories</a>
      <a routerLink="/admin/products" routerLinkActive="active">Products</a>
      <button class="btn btn-sm btn-outline" (click)="logout()">Logout</button>
    </div>
  </nav>
  <main class="main-content"><router-outlet></router-outlet></main>
` })
export class AdminLayoutComponent {
  constructor(private auth: AuthService, private router: Router) {}
  logout() { this.auth.logout().subscribe(() => this.router.navigate(['/auth/login'])); }
}
