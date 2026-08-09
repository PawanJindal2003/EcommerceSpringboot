import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { SellerService } from '../../../core/services/seller.service';
import { SellerViewProfileDTO } from '../../../core/models/user.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';
import { PASSWORD_PATTERN } from '../../../core/models/api.model';

@Component({ selector: 'app-seller-profile', templateUrl: './profile.component.html' })
export class SellerProfileComponent implements OnInit {
  profile: SellerViewProfileDTO | null = null;
  loading = false; error = ''; message = '';
  profileForm = this.fb.group({ firstName: [''], lastName: [''], companyName: [''], companyContact: [''] });
  passwordForm = this.fb.group({
    oldPassword: ['', Validators.required],
    password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', Validators.required]
  });
  addressForm = this.fb.group({ city: [''], state: [''], country: [''], addressLine: [''], zipCode: [''] });
  profilePic?: File;
  constructor(private seller: SellerService, private fb: FormBuilder, private err: ErrorHandlerService) {}
  ngOnInit() { this.load(); }
  load() {
    this.seller.getProfile().subscribe({
      next: p => { this.profile = p; this.profileForm.patchValue({ companyName: p.companyName, companyContact: p.companyContact }); this.addressForm.patchValue({ city: p.city, state: p.state, country: p.country, addressLine: p.addressLine, zipCode: p.zipCode }); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  onFile(e: Event) { this.profilePic = (e.target as HTMLInputElement).files?.[0]; }
  updateProfile() {
    this.seller.updateProfile(this.profileForm.value as any, this.profilePic).subscribe({
      next: msg => { this.message = msg; this.load(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  updatePassword() {
    if (this.passwordForm.value.password !== this.passwordForm.value.confirmPassword) { this.error = 'Passwords must match'; return; }
    this.seller.updatePassword(this.passwordForm.value as any).subscribe({
      next: msg => { this.message = msg; this.passwordForm.reset(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  updateAddress() {
    if (!this.profile?.addressId) return;
    this.seller.updateAddress(this.profile.addressId, this.addressForm.value as any).subscribe({
      next: msg => { this.message = msg; this.load(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
}
