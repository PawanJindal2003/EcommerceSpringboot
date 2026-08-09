import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Role } from '../models/api.model';
import {
  CustomerRegisterRequest,
  ForgetPasswordRequest,
  LoginRequest,
  ResetPasswordRequest,
  SellerRegisterRequest
} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly roleKey = 'ecommerce_role';
  private readonly emailKey = 'ecommerce_email';
  private roleSubject = new BehaviorSubject<Role | null>(this.getStoredRole());
  role$ = this.roleSubject.asObservable();

  constructor(private api: ApiService, private router: Router) {}

  get isLoggedIn(): boolean {
    return !!this.getStoredRole();
  }

  get currentRole(): Role | null {
    return this.getStoredRole();
  }

  get currentEmail(): string | null {
    return localStorage.getItem(this.emailKey);
  }

  login(request: LoginRequest): Observable<Role> {
    return this.api.post<string[]>('/auth/login', request).pipe(
      tap(() => localStorage.setItem(this.emailKey, request.email)),
      switchMap(() => this.detectRole()),
      tap(role => this.setRole(role))
    );
  }

  logout(): Observable<string> {
    return this.api.post<null>('/auth/logout', {}).pipe(
      tap(() => this.clearSession()),
      map(res => res.successMessage || 'Logged out')
    );
  }

  registerCustomer(request: CustomerRegisterRequest): Observable<string> {
    return this.api.post<null>('/auth/customer/register', request).pipe(
      map(res => res.successMessage || 'Registration successful')
    );
  }

  registerSeller(request: SellerRegisterRequest): Observable<string> {
    return this.api.post<null>('/auth/seller/register', request).pipe(
      map(res => res.successMessage || 'Registration successful')
    );
  }

  activateCustomer(token: string): Observable<string> {
    return this.api.put<null>(`/auth/customer/activate/${token}`, {}).pipe(
      map(res => res.successMessage || 'Account activated')
    );
  }

  resendActivation(email: string): Observable<string> {
    return this.api.post<null>(`/auth/customer/resend-activation-link?email=${encodeURIComponent(email)}`, {}).pipe(
      map(res => res.successMessage || 'Activation link sent')
    );
  }

  forgetPassword(request: ForgetPasswordRequest): Observable<string> {
    return this.api.post<null>('/auth/forget-password', request).pipe(
      map(res => res.successMessage || 'Reset link sent')
    );
  }

  resetPassword(request: ResetPasswordRequest): Observable<string> {
    return this.api.patch<null>('/auth/reset-password', request).pipe(
      map(res => res.successMessage || 'Password reset successful')
    );
  }

  navigateByRole(role: Role): void {
    switch (role) {
      case 'ADMIN': this.router.navigate(['/admin']); break;
      case 'SELLER': this.router.navigate(['/seller']); break;
      case 'CUSTOMER': this.router.navigate(['/shop']); break;
    }
  }

  clearSession(): void {
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.emailKey);
    this.roleSubject.next(null);
  }

  private detectRole(): Observable<Role> {
    return this.api.get<unknown>('/admin/customers', { page: 0, size: 1 }).pipe(
      map(() => 'ADMIN' as Role),
      catchError(() =>
        this.api.get<unknown[]>('/customer/me').pipe(
          map(() => 'CUSTOMER' as Role),
          catchError(() =>
            this.api.get<unknown[]>('/seller/me').pipe(
              map(() => 'SELLER' as Role),
              catchError(() => throwError(() => new Error('Unable to determine user role')))
            )
          )
        )
      )
    );
  }

  private setRole(role: Role): void {
    localStorage.setItem(this.roleKey, role);
    this.roleSubject.next(role);
  }

  private getStoredRole(): Role | null {
    const role = localStorage.getItem(this.roleKey);
    return role as Role | null;
  }
}
