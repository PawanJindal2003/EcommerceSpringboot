import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { GuestGuard } from '../../core/guards/auth.guard';
import { AuthShellComponent } from './auth-shell.component';
import { LoginComponent } from './login/login.component';
import { CustomerRegisterComponent } from './customer-register/customer-register.component';
import { SellerRegisterComponent } from './seller-register/seller-register.component';
import { ActivateComponent } from './activate/activate.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';

const routes: Routes = [
  {
    path: '',
    component: AuthShellComponent,
    children: [
      { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
      { path: 'register/customer', component: CustomerRegisterComponent, canActivate: [GuestGuard] },
      { path: 'register/seller', component: SellerRegisterComponent, canActivate: [GuestGuard] },
      { path: 'activate/:token', component: ActivateComponent },
      { path: 'activate', component: ActivateComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent, canActivate: [GuestGuard] },
      { path: 'reset-password', component: ResetPasswordComponent },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  declarations: [AuthShellComponent, LoginComponent, CustomerRegisterComponent, SellerRegisterComponent, ActivateComponent, ForgotPasswordComponent, ResetPasswordComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class AuthModule {}
