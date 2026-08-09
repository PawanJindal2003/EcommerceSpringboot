import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { PaginationParams } from '../models/api.model';
import {
  CategoryMetaFieldsDTO,
  CategoryResponseDTO,
  CustomerCategoryResponseDTO,
  CustomerFilterCategoryDTO,
  MetadataValueCategoryRequest,
  UpdateCategoryRequest
} from '../models/category.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  constructor(private api: ApiService) {}

  // Admin
  addMetaDataField(value: string): Observable<string> {
    return this.api.post<unknown>('/category/add-categoryMetaDataField', { value }).pipe(
      map(r => r.successMessage || 'Field added')
    );
  }

  getAllMetaDataFields(pageNo = 0, pageSize = 10, direction = 'ASC', sortField = 'id', name?: string): Observable<CategoryMetaFieldsDTO> {
    const params: Record<string, string | number> = { pageNo, pageSize, direction, sortField };
    if (name) params.name = name;
    return this.api.get<CategoryMetaFieldsDTO>('/category/all-categoryMetaDataField', params).pipe(map(r => r.data));
  }

  addCategory(name: string, parentCategoryId?: string): Observable<string> {
    const params: Record<string, string> = { name };
    if (parentCategoryId) params.parentCategoryId = parentCategoryId;
    return this.api.post<null>('/category/add-category', null, params).pipe(map(r => r.successMessage || 'Category added'));
  }

  getCategory(categoryId: string): Observable<CategoryResponseDTO> {
    return this.api.get<CategoryResponseDTO>(`/category/${categoryId}`).pipe(map(r => r.data));
  }

  getAllCategories(pageNo = 0, pageSize = 10, direction = 'ASC', sortField = 'id', name?: string): Observable<CategoryResponseDTO[]> {
    const params: Record<string, string | number> = { pageNo, pageSize, direction, sortField };
    if (name) params.name = name;
    return this.api.get<CategoryResponseDTO[]>('/category/all-categories', params).pipe(map(r => r.data ?? []));
  }

  updateCategory(request: UpdateCategoryRequest): Observable<string> {
    return this.api.put<null>('/category/update-category', request).pipe(map(r => r.successMessage || 'Updated'));
  }

  addMetadataCategory(request: MetadataValueCategoryRequest): Observable<string> {
    return this.api.post<null>('/category/add-metadata-category', request).pipe(map(r => r.successMessage || 'Metadata added'));
  }

  updateMetadataCategory(request: MetadataValueCategoryRequest): Observable<string> {
    return this.api.put<null>('/category/update-metadata-category', request).pipe(map(r => r.successMessage || 'Metadata updated'));
  }

  // Seller
  getSellerCategories(): Observable<CategoryResponseDTO[]> {
    return this.api.get<CategoryResponseDTO[]>('/category/seller/all-categories').pipe(map(r => r.data ?? []));
  }

  // Customer
  getCustomerRootCategories(): Observable<CustomerCategoryResponseDTO[]> {
    return this.api.get<CustomerCategoryResponseDTO[]>('/category/customer/categories').pipe(map(r => r.data ?? []));
  }

  getCustomerSubCategories(categoryId: string): Observable<CustomerCategoryResponseDTO[]> {
    return this.api.get<CustomerCategoryResponseDTO[]>(`/category/customer/${categoryId}/categories`).pipe(map(r => r.data ?? []));
  }

  getCustomerFilterCategory(categoryId: string): Observable<CustomerFilterCategoryDTO> {
    return this.api.get<CustomerFilterCategoryDTO>(`/category/customer/${categoryId}`).pipe(map(r => r.data));
  }
}
