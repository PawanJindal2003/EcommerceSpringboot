import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { SellerProductDTO, SellerProductVariationDTO } from '../../../core/models/product.model';
import { CategoryResponseDTO } from '../../../core/models/category.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-seller-products', templateUrl: './products.component.html' })
export class SellerProductsComponent implements OnInit {
  products: SellerProductDTO[] = [];
  variations: SellerProductVariationDTO[] = [];
  categories: CategoryResponseDTO[] = [];
  selectedProductId = '';
  loading = false; error = ''; message = '';
  showAddProduct = false; showAddVariation = false;
  query = ''; page = 0; size = 10;
  productForm = this.fb.group({ name: ['', Validators.required], brand: ['', Validators.required], categoryId: ['', Validators.required], description: [''], isCancellable: [true], isReturnable: [true] });
  variationForm = this.fb.group({ productId: ['', Validators.required], quantityAvailable: [0, Validators.required], price: [0, Validators.required], metadata: ['{}'] });
  primaryImage?: File; secondaryImages: File[] = [];
  constructor(private product: ProductService, private category: CategoryService, private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private err: ErrorHandlerService) {}
  ngOnInit() {
    this.loadProducts();
    this.category.getSellerCategories().subscribe({
      next: c => this.categories = this.flattenLeafCategories(c),
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
    this.route.queryParams.subscribe(p => { if (p.productId) { this.selectedProductId = p.productId; this.loadVariations(); } });
  }
  flattenLeafCategories(cats: CategoryResponseDTO[]): CategoryResponseDTO[] {
    const result: CategoryResponseDTO[] = [];
    const walk = (list: CategoryResponseDTO[]) => {
      list.forEach(c => {
        if (!c.subCategories?.length) result.push(c);
        else walk(c.subCategories.map(sc => ({ id: sc.id, name: sc.name, parentChain: [], subCategories: [], metaDataFields: c.metaDataFields })));
      });
    };
    walk(cats);
    return result;
  }
  loadProducts() {
    this.loading = true;
    this.product.getSellerProducts({ page: this.page, size: this.size, query: this.query || undefined }).subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  loadVariations() {
    if (!this.selectedProductId) return;
    this.product.getSellerProductVariations(this.selectedProductId, { page: 0, size: 20 }).subscribe({
      next: data => this.variations = data,
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  selectProduct(id: string) {
    this.selectedProductId = id;
    this.variationForm.patchValue({ productId: id });
    this.loadVariations();
  }
  addProduct() {
    if (this.productForm.invalid) return;
    this.product.addProduct(this.productForm.value as any).subscribe({
      next: msg => { this.message = msg; this.showAddProduct = false; this.loadProducts(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  onPrimaryImage(e: Event) { this.primaryImage = (e.target as HTMLInputElement).files?.[0]; }
  onSecondaryImages(e: Event) { this.secondaryImages = Array.from((e.target as HTMLInputElement).files || []); }
  addVariation() {
    if (this.variationForm.invalid || !this.primaryImage) { this.error = 'Primary image is required'; return; }
    const metadataRaw = (this.variationForm.value.metadata || '').trim();
    if (!metadataRaw || metadataRaw === '{}') {
      this.error = 'Metadata is required (e.g. {"Color":"Red"})';
      return;
    }
    try {
      JSON.parse(metadataRaw);
    } catch {
      this.error = 'Metadata must be valid JSON (e.g. {"Color":"Red"})';
      return;
    }
    const fd = new FormData();
    fd.append('productId', this.variationForm.value.productId!);
    fd.append('quantityAvailable', String(this.variationForm.value.quantityAvailable));
    fd.append('price', String(this.variationForm.value.price));
    fd.append('metadata', metadataRaw);
    fd.append('primaryImage', this.primaryImage);
    this.secondaryImages.forEach(f => fd.append('secondaryImages', f));
    this.product.addProductVariation(fd).subscribe({
      next: msg => { this.message = msg; this.showAddVariation = false; if (this.selectedProductId) this.loadVariations(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  deleteProduct(id: string) {
    if (!confirm('Delete this product?')) return;
    this.product.deleteSellerProduct(id).subscribe({
      next: msg => { this.message = msg; this.loadProducts(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
}
