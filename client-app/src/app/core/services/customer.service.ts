import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Address } from '../models/api.model';
import { UpdatePasswordRequest } from '../models/auth.model';
import {
  CustomerViewProfileDTO,
  UpdateAddressRequest,
  UpdateProfileRequest,
  ViewAddressDTO
} from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  constructor(private api: ApiService) {}

  getProfile(): Observable<CustomerViewProfileDTO> {
    return this.api.get<CustomerViewProfileDTO[]>('/customer/me').pipe(map(r => r.data[0]));
  }

  getAddresses(): Observable<ViewAddressDTO[]> {
    return this.api.get<ViewAddressDTO[]>('/customer/addresses').pipe(map(r => r.data));
  }

  updateProfile(profile: UpdateProfileRequest, profilePic?: File): Observable<string> {
    const formData = new FormData();
    Object.entries(profile).forEach(([key, value]) => {
      if (value !== undefined && value !== null) formData.append(key, String(value));
    });
    if (profilePic) formData.append('profilePic', profilePic);
    return this.api.putForm<null>('/customer/update-profile', formData).pipe(map(r => r.successMessage || 'Profile updated'));
  }

  updatePassword(request: UpdatePasswordRequest): Observable<string> {
    return this.api.put<null>('/customer/update-password', request).pipe(map(r => r.successMessage || 'Password updated'));
  }

  addAddress(address: Address): Observable<string> {
    return this.api.post<null>('/customer/add-address', address).pipe(map(r => r.successMessage || 'Address added'));
  }

  deleteAddress(addressId: string): Observable<string> {
    return this.api.delete<null>(`/customer/delete-address/${addressId}`).pipe(map(r => r.successMessage || 'Address deleted'));
  }

  updateAddress(addressId: string, request: UpdateAddressRequest): Observable<string> {
    return this.api.put<null>(`/customer/update-address/${addressId}`, request).pipe(map(r => r.successMessage || 'Address updated'));
  }
}
