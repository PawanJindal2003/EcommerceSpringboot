import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginationParams, SuccessResponse } from '../models/api.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  get<T>(path: string, params?: Record<string, string | number | boolean>): Observable<SuccessResponse<T>> {
    return this.http.get<SuccessResponse<T>>(`${this.baseUrl}${path}`, {
      params: this.buildParams(params),
      withCredentials: true
    });
  }

  post<T>(path: string, body?: unknown, params?: Record<string, string | number | boolean>): Observable<SuccessResponse<T>> {
    return this.http.post<SuccessResponse<T>>(`${this.baseUrl}${path}`, body, {
      params: this.buildParams(params),
      withCredentials: true
    });
  }

  put<T>(path: string, body?: unknown): Observable<SuccessResponse<T>> {
    return this.http.put<SuccessResponse<T>>(`${this.baseUrl}${path}`, body, { withCredentials: true });
  }

  patch<T>(path: string, body?: unknown): Observable<SuccessResponse<T>> {
    return this.http.patch<SuccessResponse<T>>(`${this.baseUrl}${path}`, body, { withCredentials: true });
  }

  delete<T>(path: string): Observable<SuccessResponse<T>> {
    return this.http.delete<SuccessResponse<T>>(`${this.baseUrl}${path}`, { withCredentials: true });
  }

  postForm<T>(path: string, formData: FormData): Observable<SuccessResponse<T>> {
    return this.http.post<SuccessResponse<T>>(`${this.baseUrl}${path}`, formData, { withCredentials: true });
  }

  putForm<T>(path: string, formData: FormData): Observable<SuccessResponse<T>> {
    return this.http.put<SuccessResponse<T>>(`${this.baseUrl}${path}`, formData, { withCredentials: true });
  }

  patchForm<T>(path: string, formData: FormData): Observable<SuccessResponse<T>> {
    return this.http.patch<SuccessResponse<T>>(`${this.baseUrl}${path}`, formData, { withCredentials: true });
  }

  buildPaginationParams(p: PaginationParams): Record<string, string | number> {
    const params: Record<string, string | number> = {};
    if (p.page !== undefined) params.page = p.page;
    if (p.size !== undefined) params.size = p.size;
    if (p.sortField) params.sortField = p.sortField;
    if (p.direction) params.direction = p.direction;
    if (p.query) params.query = p.query;
    return params;
  }

  private buildParams(params?: Record<string, string | number | boolean>): HttpParams {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== undefined && value !== null && value !== '') {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }
    return httpParams;
  }
}
