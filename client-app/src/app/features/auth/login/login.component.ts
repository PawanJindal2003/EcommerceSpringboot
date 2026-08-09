import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN } from '../../../core/models/api.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loading = false;
  error = '';
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(15), Validators.pattern(PASSWORD_PATTERN)]]
  });

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) {}

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.auth.login(this.form.value as { email: string; password: string }).subscribe({
      next: (role: import('../../../core/models/api.model').Role) => {
        this.loading = false;
        this.auth.navigateByRole(role);
      },
      error: (err: unknown) => {
        this.loading = false;
        this.error = this.errorHandler.getMessages(err).join(', ');
      }
    });
  }
}
