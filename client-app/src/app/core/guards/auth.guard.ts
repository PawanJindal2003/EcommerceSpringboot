import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/api.model';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.auth.isLoggedIn) return true;
    this.router.navigate(['/auth/login']);
    return false;
  }
}

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles: Role[] = route.data.roles || [];
    const currentRole = this.auth.currentRole;
    if (currentRole && expectedRoles.includes(currentRole)) return true;
    if (currentRole) this.auth.navigateByRole(currentRole);
    else this.router.navigate(['/auth/login']);
    return false;
  }
}

@Injectable({ providedIn: 'root' })
export class GuestGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (!this.auth.isLoggedIn) return true;
    const role = this.auth.currentRole;
    if (role) this.auth.navigateByRole(role);
    return false;
  }
}
