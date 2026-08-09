import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-reset-password', templateUrl: './reset-password.component.html' })
export class ResetPasswordComponent {
  loading = false; error = ''; success = '';
  token = this.route.snapshot.queryParamMap.get('token') || '';
  form = this.fb.group({
    password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', [Validators.required]]
  });
  constructor(private fb: FormBuilder, private auth: AuthService, private route: ActivatedRoute, private router: Router, private err: ErrorHandlerService) {}
  submit() {
    if (this.form.invalid || this.form.value.password !== this.form.value.confirmPassword) { this.error = 'Passwords must match'; return; }
    this.loading = true;
    this.auth.resetPassword({ forgetPasswordToken: this.token, password: this.form.value.password!, confirmPassword: this.form.value.confirmPassword! }).subscribe({
      next: msg => { this.loading = false; this.success = msg; setTimeout(() => this.router.navigate(['/auth/login']), 2000); },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); }
    });
  }
}
