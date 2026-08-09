import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-forgot-password', templateUrl: './forgot-password.component.html' })
export class ForgotPasswordComponent {
  loading = false; error = ''; success = '';
  form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private err: ErrorHandlerService) {}
  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.forgetPassword(this.form.value as { email: string }).subscribe({
      next: msg => { this.loading = false; this.success = msg; },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); }
    });
  }
}
