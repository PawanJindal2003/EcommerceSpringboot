export interface GetAllCustomersDTO {
  id: string;
  name: string;
  email: string;
  isActive: boolean;
}

export interface GetAllSellersDTO {
  id: string;
  name: string;
  email: string;
  isActive: boolean;
  companyName: string;
  companyAddress: string;
  companyContact: string;
}

export interface CustomerViewProfileDTO {
  id: string;
  name: string;
  isActive: boolean;
  customerContact: string;
  profilePicUrl: string;
}

export interface SellerViewProfileDTO {
  id: string;
  name: string;
  isActive: boolean;
  companyContact: string;
  companyName: string;
  GST: string;
  addressId?: string;
  city: string;
  state: string;
  country: string;
  addressLine: string;
  zipCode: string;
  profilePicUrl: string;
}

export interface ViewAddressDTO {
  id: string;
  city: string;
  state: string;
  country: string;
  addressLine: string;
  zipCode: string;
  label: string;
}

export interface UpdateProfileRequest {
  firstName?: string;
  middleName?: string;
  lastName?: string;
  customerContact?: string;
  companyContact?: string;
  companyName?: string;
  profilePicUrl?: string;
}

export interface UpdateAddressRequest {
  city: string;
  state: string;
  country: string;
  addressLine: string;
  zipCode: string;
}
