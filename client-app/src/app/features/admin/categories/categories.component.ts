import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponseDTO } from '../../../core/models/category.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-admin-categories', templateUrl: './categories.component.html' })
export class AdminCategoriesComponent implements OnInit {
  categories: CategoryResponseDTO[] = [];
  metaFields: string[] = [];
  loading = false; error = ''; message = '';
  selectedCategory: CategoryResponseDTO | null = null;
  newCategoryName = ''; parentCategoryId = '';
  newMetaField = '';
  metaForm = this.fb.group({ categoryId: ['', Validators.required], metaDataFieldId: ['', Validators.required], values: ['', Validators.required] });
  constructor(private category: CategoryService, private fb: FormBuilder, private err: ErrorHandlerService) {}
  ngOnInit() { this.loadCategories(); this.loadMetaFields(); }
  loadCategories() {
    this.loading = true;
    this.category.getAllCategories().subscribe({
      next: data => { this.categories = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  loadMetaFields() {
    this.category.getAllMetaDataFields().subscribe({
      next: data => this.metaFields = data.fields || [],
      error: () => {}
    });
  }
  addCategory() {
    if (!this.newCategoryName) return;
    this.category.addCategory(this.newCategoryName, this.parentCategoryId || undefined).subscribe({
      next: msg => { this.message = msg; this.newCategoryName = ''; this.loadCategories(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  addMetaField() {
    if (!this.newMetaField) return;
    this.category.addMetaDataField(this.newMetaField).subscribe({
      next: msg => { this.message = msg; this.newMetaField = ''; this.loadMetaFields(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  viewCategory(id: string) {
    this.category.getCategory(id).subscribe({
      next: data => this.selectedCategory = data,
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  addMetadata() {
    if (this.metaForm.invalid) return;
    const v = this.metaForm.value;
    this.category.addMetadataCategory({ categoryId: v.categoryId!, metaDataFieldId: v.metaDataFieldId!, values: v.values!.split(',').map((s: string) => s.trim()) }).subscribe({
      next: msg => { this.message = msg; if (v.categoryId) this.viewCategory(v.categoryId); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
}
