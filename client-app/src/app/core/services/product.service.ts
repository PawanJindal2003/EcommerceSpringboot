import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { PaginationParams } from '../models/api.model';
import {
  AddProductRequest,
  AdminProductDTO,
  CustomerAllProductsDTO,
  CustomerProductDTO,
  SellerProductDTO,
  SellerProductVariationDTO,
  UpdateProductRequest
} from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(private api: ApiService) {}

  // Seller
  addProduct(request: AddProductRequest): Observable<string> {
    return this.api.post<null>('/product/add-product', request).pipe(map(r => r.successMessage || 'Product added'));
  }

  addProductVariation(formData: FormData): Observable<string> {
    return this.api.postForm<null>('/product/add-product-variation', formData).pipe(map(r => r.successMessage || 'Variation added'));
  }

  getSellerProduct(productId: string): Observable<SellerProductDTO> {
    return this.api.get<SellerProductDTO>(`/product/seller/${productId}`).pipe(map(r => r.data));
  }

  getSellerProductVariation(variationId: string): Observable<SellerProductVariationDTO> {
    return this.api.get<SellerProductVariationDTO>(`/product/product-variation/${variationId}`).pipe(map(r => r.data));
  }

  getSellerProducts(params: PaginationParams): Observable<SellerProductDTO[]> {
    return this.api.get<SellerProductDTO[]>('/product/seller/all-products', this.api.buildPaginationParams(params)).pipe(map(r => r.data ?? []));
  }

  getSellerProductVariations(productId: string, params: PaginationParams): Observable<SellerProductVariationDTO[]> {
    return this.api.get<SellerProductVariationDTO[]>(
      `/product/seller/${productId}/all-product-variations`,
      this.api.buildPaginationParams(params)
    ).pipe(map(r => r.data));
  }

  deleteSellerProduct(productId: string): Observable<string> {
    return this.api.delete<null>(`/product/seller/${productId}`).pipe(map(r => r.successMessage || 'Deleted'));
  }

  updateSellerProduct(productId: string, request: UpdateProductRequest): Observable<string> {
    return this.api.put<null>(`/product/update-product/${productId}`, request).pipe(map(r => r.successMessage || 'Updated'));
  }

  updateProductVariation(variationId: string, formData: FormData): Observable<string> {
    return this.api.putForm<null>(`/product/update-product-variation/${variationId}`, formData).pipe(map(r => r.successMessage || 'Updated'));
  }

  // Customer
  getCustomerProduct(productId: string): Observable<CustomerProductDTO> {
    return this.api.get<CustomerProductDTO>(`/product/customer/${productId}`).pipe(map(r => r.data));
  }

  getCustomerProductsByCategory(categoryId: string, params: PaginationParams): Observable<CustomerAllProductsDTO[]> {
    return this.api.get<CustomerAllProductsDTO[]>(
      `/product/customer/all-products/${categoryId}`,
      this.api.buildPaginationParams(params)
    ).pipe(map(r => r.data ?? []));
  }

  getSimilarProducts(productId: string, params: PaginationParams): Observable<CustomerProductDTO[]> {
    return this.api.get<CustomerProductDTO[]>(
      `/product/customer/similar-products/${productId}`,
      this.api.buildPaginationParams(params)
    ).pipe(map(r => r.data));
  }

  // Admin
  getAdminProduct(productId: string): Observable<AdminProductDTO> {
    return this.api.get<AdminProductDTO>(`/product/admin/${productId}`).pipe(map(r => r.data));
  }

  getAdminProducts(params: PaginationParams): Observable<AdminProductDTO[]> {
    return this.api.get<AdminProductDTO[]>('/product/admin/product-list', this.api.buildPaginationParams(params)).pipe(map(r => r.data ?? []));
  }

  activateProduct(productId: string): Observable<string> {
    return this.api.put<null>(`/product/admin/activate/${productId}`, {}).pipe(map(r => r.successMessage || 'Activated'));
  }

  deactivateProduct(productId: string): Observable<string> {
    return this.api.put<null>(`/product/admin/de-activate/${productId}`, {}).pipe(map(r => r.successMessage || 'Deactivated'));
  }
}
