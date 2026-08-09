import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CustomerAllProductsDTO, CustomerProductDTO } from '../../../core/models/product.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-customer-products', templateUrl: './products.component.html' })
export class CustomerProductsComponent implements OnInit {
  products: CustomerAllProductsDTO[] = [];
  selectedProduct: CustomerProductDTO | null = null;
  similarProducts: CustomerProductDTO[] = [];
  categoryId = '';
  loading = false; error = '';
  query = ''; page = 0; size = 12;
  constructor(private route: ActivatedRoute, private router: Router, private product: ProductService, private err: ErrorHandlerService) {}
  ngOnInit() {
    this.route.paramMap.subscribe(p => { this.categoryId = p.get('categoryId') || ''; this.load(); });
  }
  load() {
    if (!this.categoryId) return;
    this.loading = true;
    this.product.getCustomerProductsByCategory(this.categoryId, { page: this.page, size: this.size, query: this.query || undefined }).subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  viewProduct(id: string) {
    this.product.getCustomerProduct(id).subscribe({
      next: data => { this.selectedProduct = data; this.loadSimilar(id); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  loadSimilar(id: string) {
    this.product.getSimilarProducts(id, { page: 0, size: 4 }).subscribe({ next: data => this.similarProducts = data });
  }
  closeDetail() { this.selectedProduct = null; this.similarProducts = []; }
}
