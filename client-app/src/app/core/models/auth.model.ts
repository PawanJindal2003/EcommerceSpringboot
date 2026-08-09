import { Address } from './api.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface CustomerRegisterRequest {
  email: string;
  customerContact: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  addresses?: Address[];
}

export interface SellerRegisterRequest {
  email: string;
  password: string;
  confirmPassword: string;
  gst: string;
  companyName: string;
  companyContact: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  addresses?: Address[];
}

export interface ForgetPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  forgetPasswordToken: string;
  password: string;
  confirmPassword: string;
}

export interface UpdatePasswordRequest {
  oldPassword: string;
  password: string;
  confirmPassword: string;
}
