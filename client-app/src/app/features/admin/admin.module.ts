import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { AuthGuard, RoleGuard } from '../../core/guards/auth.guard';
import { AdminLayoutComponent } from './admin-layout.component';
import { AdminDashboardComponent } from './dashboard/dashboard.component';
import { AdminCustomersComponent } from './customers/customers.component';
import { AdminSellersComponent } from './sellers/sellers.component';
import { AdminCategoriesComponent } from './categories/categories.component';
import { AdminProductsComponent } from './products/products.component';

const routes: Routes = [
  {
    path: '', component: AdminLayoutComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] },
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'customers', component: AdminCustomersComponent },
      { path: 'sellers', component: AdminSellersComponent },
      { path: 'categories', component: AdminCategoriesComponent },
      { path: 'products', component: AdminProductsComponent }
    ]
  }
];

@NgModule({
  declarations: [AdminLayoutComponent, AdminDashboardComponent, AdminCustomersComponent, AdminSellersComponent, AdminCategoriesComponent, AdminProductsComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class AdminModule {}
