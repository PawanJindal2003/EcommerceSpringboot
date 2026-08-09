import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { AdminProductDTO } from '../../../core/models/product.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-admin-products', templateUrl: './products.component.html' })
export class AdminProductsComponent implements OnInit {
  products: AdminProductDTO[] = [];
  selected: AdminProductDTO | null = null;
  loading = false; error = ''; message = '';
  query = ''; page = 0; size = 10;
  constructor(private product: ProductService, private err: ErrorHandlerService) {}
  ngOnInit() { this.load(); }
  load() {
    this.loading = true;
    this.product.getAdminProducts({ page: this.page, size: this.size, query: this.query || undefined }).subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  view(id: string) {
    this.product.getAdminProduct(id).subscribe({
      next: data => this.selected = data,
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  toggle(p: AdminProductDTO) {
    const action = p.isActive ? this.product.deactivateProduct(p.id) : this.product.activateProduct(p.id);
    action.subscribe({ next: msg => { this.message = msg; this.load(); }, error: e => this.error = this.err.getMessages(e).join(', ') });
  }
}
