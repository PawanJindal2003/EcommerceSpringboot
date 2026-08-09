import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { CustomerCategoryResponseDTO, CustomerFilterCategoryDTO } from '../../../core/models/category.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-customer-catalog', templateUrl: './catalog.component.html' })
export class CustomerCatalogComponent implements OnInit {
  categories: CustomerCategoryResponseDTO[] = [];
  subCategories: CustomerCategoryResponseDTO[] = [];
  filterData: CustomerFilterCategoryDTO | null = null;
  selectedCategoryId = '';
  breadcrumb: CustomerCategoryResponseDTO[] = [];
  loading = false; error = '';
  constructor(private category: CategoryService, private router: Router, private err: ErrorHandlerService) {}
  ngOnInit() { this.loadRoot(); }
  loadRoot() {
    this.loading = true;
    this.category.getCustomerRootCategories().subscribe({
      next: data => { this.categories = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  selectCategory(cat: CustomerCategoryResponseDTO) {
    this.selectedCategoryId = cat.id;
    this.breadcrumb.push(cat);
    this.category.getCustomerSubCategories(cat.id).subscribe({
      next: subs => {
        if (subs.length) this.subCategories = subs;
        else this.browseProducts(cat.id);
      },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
    this.category.getCustomerFilterCategory(cat.id).subscribe({ next: f => this.filterData = f });
  }
  browseProducts(categoryId: string) {
    this.router.navigate(['/shop/products', categoryId]);
  }
  goBack() {
    this.breadcrumb.pop();
    if (this.breadcrumb.length) this.selectCategory(this.breadcrumb[this.breadcrumb.length - 1]);
    else { this.subCategories = []; this.filterData = null; this.selectedCategoryId = ''; this.loadRoot(); }
  }
}
