import { Component } from '@angular/core';
import { FormBuilder, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { SellerRegisterRequest } from '../../../core/models/auth.model';
import { PASSWORD_PATTERN, PHONE_PATTERN, GST_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-seller-register', templateUrl: './seller-register.component.html' })
export class SellerRegisterComponent {
  loading = false; error = ''; success = '';
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15), Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', [Validators.required]],
    GST: ['', [Validators.required, Validators.pattern(GST_PATTERN)]],
    companyName: ['', Validators.required],
    companyContact: ['', [Validators.required, Validators.pattern(PHONE_PATTERN)]],
    firstName: ['', [Validators.required, Validators.minLength(3)]],
    middleName: [''],
    lastName: ['', [Validators.required, Validators.minLength(3)]],
    addresses: this.fb.array([this.createAddress()])
  });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private err: ErrorHandlerService) {}
  get addresses() { return this.form.get('addresses') as FormArray; }
  createAddress() { return this.fb.group({ city: ['', Validators.required], state: ['', Validators.required], country: ['', Validators.required], addressLine: ['', Validators.required], zipCode: ['', Validators.required], label: ['Office'] }); }
  submit() {
    this.error = '';
    if (this.form.value.password !== this.form.value.confirmPassword) {
      this.error = 'Passwords must match';
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error = this.getFormValidationMessage();
      return;
    }
    this.loading = true;
    const { GST, middleName, ...rest } = this.form.value;
    const request: SellerRegisterRequest = {
      ...rest,
      email: rest.email!,
      password: rest.password!,
      confirmPassword: rest.confirmPassword!,
      companyName: rest.companyName!,
      companyContact: rest.companyContact!,
      firstName: rest.firstName!,
      lastName: rest.lastName!,
      gst: GST!,
      addresses: rest.addresses
    };
    if (middleName) request.middleName = middleName;
    this.auth.registerSeller(request).subscribe({
      next: msg => { this.loading = false; this.success = msg + ' Admin will activate your account.'; setTimeout(() => this.router.navigate(['/auth/login']), 3000); },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); }
    });
  }

  private getFormValidationMessage(): string {
    const messages: string[] = [];
    const { email, password, GST, companyName, companyContact, firstName, lastName } = this.form.controls;
    if (email.invalid) messages.push('Enter a valid email address.');
    if (password.invalid) messages.push('Password must be 8-15 characters with uppercase, lowercase, number, and special character (!@#$%^&*).');
    if (GST.invalid) messages.push('Enter a valid 15-character GST number (e.g. 22AAAAA0000A1Z5).');
    if (companyContact.invalid) messages.push('Enter a valid phone number with country code (e.g. +91-9876543210).');
    if (firstName.invalid) messages.push('First name must be at least 3 characters.');
    if (lastName.invalid) messages.push('Last name must be at least 3 characters.');
    if (companyName.invalid) messages.push('Company name is required.');
    if (this.addresses.at(0)?.invalid) messages.push('Please complete all address fields.');
    return messages.length ? messages.join(' ') : 'Please fix the errors in the form before submitting.';
  }
}
