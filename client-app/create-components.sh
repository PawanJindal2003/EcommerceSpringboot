#!/bin/bash
BASE="/home/pawan-kumar/Documents/ECommerce/ecommerce-frontend/src/app/features"

# Customer Register
cat > "$BASE/auth/customer-register/customer-register.component.ts" << 'EOF'
import { Component } from '@angular/core';
import { FormBuilder, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN, PHONE_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-customer-register', templateUrl: './customer-register.component.html' })
export class CustomerRegisterComponent {
  loading = false; error = ''; success = '';
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    customerContact: ['', [Validators.required, Validators.pattern(PHONE_PATTERN)]],
    password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', [Validators.required]],
    firstName: ['', [Validators.required, Validators.minLength(3)]],
    middleName: [''],
    lastName: ['', [Validators.required, Validators.minLength(3)]],
    addresses: this.fb.array([this.createAddress()])
  });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private err: ErrorHandlerService) {}
  get addresses() { return this.form.get('addresses') as FormArray; }
  createAddress() { return this.fb.group({ city: ['', Validators.required], state: ['', Validators.required], country: ['', Validators.required], addressLine: ['', Validators.required], zipCode: ['', Validators.required], label: ['Home'] }); }
  addAddress() { this.addresses.push(this.createAddress()); }
  submit() {
    if (this.form.invalid || this.form.value.password !== this.form.value.confirmPassword) { this.error = 'Passwords must match'; return; }
    this.loading = true; this.error = '';
    this.auth.registerCustomer(this.form.value as any).subscribe({
      next: msg => { this.loading = false; this.success = msg; setTimeout(() => this.router.navigate(['/auth/login']), 2000); },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); }
    });
  }
}
EOF

cat > "$BASE/auth/customer-register/customer-register.component.html" << 'EOF'
<div class="auth-card wide">
  <h2>Customer Registration</h2>
  <div class="alert alert-error" *ngIf="error">{{ error }}</div>
  <div class="alert alert-success" *ngIf="success">{{ success }}</div>
  <form [formGroup]="form" (ngSubmit)="submit()">
    <div class="grid-2">
      <div class="form-group"><label>Email</label><input formControlName="email" type="email" /></div>
      <div class="form-group"><label>Phone</label><input formControlName="customerContact" placeholder="+91-9876543210" /></div>
      <div class="form-group"><label>First Name</label><input formControlName="firstName" /></div>
      <div class="form-group"><label>Middle Name</label><input formControlName="middleName" /></div>
      <div class="form-group"><label>Last Name</label><input formControlName="lastName" /></div>
      <div class="form-group"><label>Password</label><input formControlName="password" type="password" /></div>
      <div class="form-group"><label>Confirm Password</label><input formControlName="confirmPassword" type="password" /></div>
    </div>
    <h3>Address</h3>
    <div formArrayName="addresses">
      <div *ngFor="let addr of addresses.controls; let i = index" [formGroupName]="i" class="address-block">
        <div class="grid-2">
          <div class="form-group"><label>Address Line</label><input formControlName="addressLine" /></div>
          <div class="form-group"><label>City</label><input formControlName="city" /></div>
          <div class="form-group"><label>State</label><input formControlName="state" /></div>
          <div class="form-group"><label>Country</label><input formControlName="country" /></div>
          <div class="form-group"><label>Zip Code</label><input formControlName="zipCode" /></div>
          <div class="form-group"><label>Label</label><input formControlName="label" /></div>
        </div>
      </div>
    </div>
    <button type="button" class="btn btn-secondary" (click)="addAddress()">Add Another Address</button>
    <button type="submit" class="btn btn-primary" [disabled]="loading">Register</button>
  </form>
  <div class="auth-links"><a routerLink="/auth/login">Back to Login</a></div>
</div>
EOF

