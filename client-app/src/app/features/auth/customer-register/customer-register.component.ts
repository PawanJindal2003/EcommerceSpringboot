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
