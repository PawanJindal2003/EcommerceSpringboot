import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from '../../../core/services/customer.service';
import { CustomerViewProfileDTO } from '../../../core/models/user.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-customer-profile', templateUrl: './profile.component.html' })
export class CustomerProfileComponent implements OnInit {
  profile: CustomerViewProfileDTO | null = null;
  error = ''; message = '';
  profileForm = this.fb.group({ firstName: [''], lastName: [''], customerContact: [''] });
  passwordForm = this.fb.group({
    oldPassword: ['', Validators.required],
    password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', Validators.required]
  });
  profilePic?: File;
  constructor(private customer: CustomerService, private fb: FormBuilder, private err: ErrorHandlerService) {}
  ngOnInit() { this.load(); }
  load() {
    this.customer.getProfile().subscribe({
      next: p => { this.profile = p; this.profileForm.patchValue({ customerContact: p.customerContact }); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  onFile(e: Event) { this.profilePic = (e.target as HTMLInputElement).files?.[0]; }
  updateProfile() {
    this.customer.updateProfile(this.profileForm.value as any, this.profilePic).subscribe({
      next: msg => { this.message = msg; this.load(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  updatePassword() {
    if (this.passwordForm.value.password !== this.passwordForm.value.confirmPassword) { this.error = 'Passwords must match'; return; }
    this.customer.updatePassword(this.passwordForm.value as any).subscribe({
      next: msg => { this.message = msg; this.passwordForm.reset(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
}
