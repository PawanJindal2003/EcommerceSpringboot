import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { AuthGuard, RoleGuard } from '../../core/guards/auth.guard';
import { SellerLayoutComponent } from './seller-layout.component';
import { SellerDashboardComponent } from './dashboard/dashboard.component';
import { SellerProfileComponent } from './profile/profile.component';
import { SellerProductsComponent } from './products/products.component';

const routes: Routes = [
  {
    path: '', component: SellerLayoutComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['SELLER'] },
    children: [
      { path: '', component: SellerDashboardComponent },
      { path: 'profile', component: SellerProfileComponent },
      { path: 'products', component: SellerProductsComponent }
    ]
  }
];

@NgModule({
  declarations: [SellerLayoutComponent, SellerDashboardComponent, SellerProfileComponent, SellerProductsComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class SellerModule {}
