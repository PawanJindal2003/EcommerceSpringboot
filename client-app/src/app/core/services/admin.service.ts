import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { PaginationParams } from '../models/api.model';
import { GetAllCustomersDTO, GetAllSellersDTO } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private api: ApiService) {}

  getCustomers(page = 0, size = 10, email?: string, sortField = 'id', direction = 'ASC'): Observable<GetAllCustomersDTO[]> {
    const params: Record<string, string | number> = { page, size, sortField, direction };
    if (email) params.email = email;
    return this.api.get<GetAllCustomersDTO[]>('/admin/customers', params).pipe(map(r => r.data));
  }

  getSellers(page = 0, size = 10, email?: string): Observable<GetAllSellersDTO[]> {
    const params: Record<string, string | number> = { page, size };
    if (email) params.email = email;
    return this.api.get<GetAllSellersDTO[]>('/admin/sellers', params).pipe(map(r => r.data));
  }

  activateUser(userId: string): Observable<string> {
    return this.api.patch<null>(`/admin/activate/user/${userId}`, {}).pipe(map(r => r.successMessage || 'Activated'));
  }

  deactivateUser(userId: string): Observable<string> {
    return this.api.patch<null>(`/admin/deactivate/user/${userId}`, {}).pipe(map(r => r.successMessage || 'Deactivated'));
  }
}
