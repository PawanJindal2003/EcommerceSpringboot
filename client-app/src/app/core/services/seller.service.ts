import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { UpdatePasswordRequest } from '../models/auth.model';
import { SellerViewProfileDTO, UpdateAddressRequest, UpdateProfileRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class SellerService {
  constructor(private api: ApiService) {}

  getProfile(): Observable<SellerViewProfileDTO> {
    return this.api.get<SellerViewProfileDTO[]>('/seller/me').pipe(map(r => r.data[0]));
  }

  updateProfile(profile: UpdateProfileRequest, profilePic?: File): Observable<string> {
    const formData = new FormData();
    Object.entries(profile).forEach(([key, value]) => {
      if (value !== undefined && value !== null) formData.append(key, String(value));
    });
    if (profilePic) formData.append('profilePic', profilePic);
    return this.api.putForm<null>('/seller/update-profile', formData).pipe(map(r => r.successMessage || 'Profile updated'));
  }

  updatePassword(request: UpdatePasswordRequest): Observable<string> {
    return this.api.patch<null>('/seller/update-password', request).pipe(map(r => r.successMessage || 'Password updated'));
  }

  updateAddress(addressId: string, request: UpdateAddressRequest): Observable<string> {
    return this.api.patch<null>(`/seller/update-address/${addressId}`, request).pipe(map(r => r.successMessage || 'Address updated'));
  }
}
