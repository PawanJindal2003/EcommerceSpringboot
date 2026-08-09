import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-activate', templateUrl: './activate.component.html' })
export class ActivateComponent implements OnInit {
  loading = false; error = ''; success = ''; token = '';
  resendForm = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  showResend = false;
  constructor(private route: ActivatedRoute, private auth: AuthService, private router: Router, private fb: FormBuilder, private err: ErrorHandlerService) {}
  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('token') || '';
    if (this.token) this.activate();
    else this.showResend = true;
  }
  activate() {
    this.loading = true;
    this.auth.activateCustomer(this.token).subscribe({
      next: msg => { this.loading = false; this.success = msg; setTimeout(() => this.router.navigate(['/auth/login']), 2000); },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); this.showResend = true; }
    });
  }
  resend() {
    if (this.resendForm.invalid) return;
    this.loading = true;
    this.auth.resendActivation(this.resendForm.value.email!).subscribe({
      next: msg => { this.loading = false; this.success = msg; },
      error: e => { this.loading = false; this.error = this.err.getMessages(e).join(', '); }
    });
  }
}
