import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleRedirectGuard } from './core/guards/role-redirect.guard';

const routes: Routes = [
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: 'admin', loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule) },
  { path: 'seller', loadChildren: () => import('./features/seller/seller.module').then(m => m.SellerModule) },
  { path: 'shop', loadChildren: () => import('./features/customer/customer.module').then(m => m.CustomerModule) },
  { path: '', canActivate: [RoleRedirectGuard], children: [] },
  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
