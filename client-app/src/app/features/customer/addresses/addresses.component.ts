import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from '../../../core/services/customer.service';
import { ViewAddressDTO } from '../../../core/models/user.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-customer-addresses', templateUrl: './addresses.component.html' })
export class CustomerAddressesComponent implements OnInit {
  addresses: ViewAddressDTO[] = [];
  error = ''; message = '';
  showForm = false; editingId = '';
  form = this.fb.group({ city: ['', Validators.required], state: ['', Validators.required], country: ['', Validators.required], addressLine: ['', Validators.required], zipCode: ['', Validators.required], label: ['Home'] });
  constructor(private customer: CustomerService, private fb: FormBuilder, private err: ErrorHandlerService) {}
  ngOnInit() { this.load(); }
  load() {
    this.customer.getAddresses().subscribe({
      next: data => this.addresses = data,
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  edit(addr: ViewAddressDTO) {
    this.editingId = addr.id; this.showForm = true;
    this.form.patchValue(addr);
  }
  resetForm() { this.showForm = false; this.editingId = ''; this.form.reset({ label: 'Home' }); }
  submit() {
    if (this.form.invalid) return;
    const req = this.form.value as any;
    const action = this.editingId ? this.customer.updateAddress(this.editingId, req) : this.customer.addAddress(req);
    action.subscribe({
      next: msg => { this.message = msg; this.resetForm(); this.load(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
  delete(id: string) {
    if (!confirm('Delete this address?')) return;
    this.customer.deleteAddress(id).subscribe({
      next: msg => { this.message = msg; this.load(); },
      error: e => this.error = this.err.getMessages(e).join(', ')
    });
  }
}
