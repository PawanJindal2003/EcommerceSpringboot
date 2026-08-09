import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { AuthGuard, RoleGuard } from '../../core/guards/auth.guard';
import { CustomerLayoutComponent } from './customer-layout.component';
import { CustomerDashboardComponent } from './dashboard/dashboard.component';
import { CustomerCatalogComponent } from './catalog/catalog.component';
import { CustomerProductsComponent } from './products/products.component';
import { CustomerProfileComponent } from './profile/profile.component';
import { CustomerAddressesComponent } from './addresses/addresses.component';

const routes: Routes = [
  {
    path: '', component: CustomerLayoutComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['CUSTOMER'] },
    children: [
      { path: '', component: CustomerDashboardComponent },
      { path: 'catalog', component: CustomerCatalogComponent },
      { path: 'products/:categoryId', component: CustomerProductsComponent },
      { path: 'profile', component: CustomerProfileComponent },
      { path: 'addresses', component: CustomerAddressesComponent }
    ]
  }
];

@NgModule({
  declarations: [CustomerLayoutComponent, CustomerDashboardComponent, CustomerCatalogComponent, CustomerProductsComponent, CustomerProfileComponent, CustomerAddressesComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class CustomerModule {}
