export type Role = 'ADMIN' | 'SELLER' | 'CUSTOMER';

export interface SuccessResponse<T = unknown> {
  data: T;
  httpStatusCode: string;
  responseTime: string;
  successMessage: string | null;
}

export interface ErrorResponse {
  data: unknown;
  httpStatusCode: string;
  responseTime: string;
  errorMessages: string[];
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sortField?: string;
  direction?: 'ASC' | 'DESC';
  query?: string;
}

export interface Address {
  id?: string;
  city: string;
  state: string;
  country: string;
  addressLine: string;
  zipCode: string;
  label?: string;
}

export const PASSWORD_PATTERN = '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).*$';
export const PHONE_PATTERN = '^\\+[1-9]{1}[0-9]{0,3}[-\\s]?[1-9]{1}[0-9]{6,11}$';
export const GST_PATTERN = '^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$';
