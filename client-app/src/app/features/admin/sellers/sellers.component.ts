import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';
import { GetAllSellersDTO } from '../../../core/models/user.model';
import { ErrorHandlerService } from '../../../shared/error-handler.service';

@Component({ selector: 'app-admin-sellers', templateUrl: './sellers.component.html' })
export class AdminSellersComponent implements OnInit {
  sellers: GetAllSellersDTO[] = [];
  loading = false; error = ''; message = '';
  email = ''; page = 0; size = 10;
  constructor(private admin: AdminService, private err: ErrorHandlerService) {}
  ngOnInit() { this.load(); }
  load() {
    this.loading = true;
    this.admin.getSellers(this.page, this.size, this.email || undefined).subscribe({
      next: data => { this.sellers = data; this.loading = false; },
      error: e => { this.error = this.err.getMessages(e).join(', '); this.loading = false; }
    });
  }
  search() { this.page = 0; this.load(); }
  toggle(user: GetAllSellersDTO) {
    const action = user.isActive ? this.admin.deactivateUser(user.id) : this.admin.activateUser(user.id);
    action.subscribe({ next: msg => { this.message = msg; this.load(); }, error: e => this.error = this.err.getMessages(e).join(', ') });
  }
}
