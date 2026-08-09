import { CategoryResponseDTO } from './category.model';

export interface AddProductRequest {
  name: string;
  brand: string;
  categoryId: string;
  description?: string;
  isCancellable?: boolean;
  isReturnable?: boolean;
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  isCancellable?: boolean;
  isReturnable?: boolean;
}

export interface SellerProductDTO {
  id: string;
  name: string;
  description: string;
  isCancellable: boolean;
  isReturnable: boolean;
  brand: string;
  isActive: boolean;
  category: CategoryResponseDTO;
}

export interface SellerProductVariationDTO {
  id: string;
  quantityAvailable: number;
  price: number;
  metaData: Record<string, string>;
  primaryImageName: string;
  secondaryImageNames: string[];
  isActive: boolean;
  product: SellerProductDTO;
}

export interface CustomerProductCategoryDTO {
  categoryId: string;
  categoryName: string;
  categoryParentId: string;
}

export interface CustomerProductVariationDTO {
  metadata: Record<string, string>;
  price: number;
  primaryImage: string;
  secondaryImages: string[];
}

export interface CustomerProductDTO {
  name: string;
  brand: string;
  description: string;
  isCancellable: boolean;
  isReturnable: boolean;
  category: CustomerProductCategoryDTO[];
  productVariation: CustomerProductVariationDTO[];
}

export interface CustomerAllProductsVariationsDTO {
  productVariationId: string;
  price: number;
  primaryImage: string;
}

export interface CustomerAllProductsDTO {
  id: string;
  name: string;
  brand: string;
  retailer: string;
  productVariations: CustomerAllProductsVariationsDTO[];
  categories: CustomerProductCategoryDTO;
}

export interface AdminProductVariationDTO {
  productVariationId: string;
  primaryImage: string;
}

export interface SellerDetailsDTO {
  sellerId: string;
  name: string;
  companyName: string;
  companyContact: string;
  GST: string;
  isActiveSeller: boolean;
}

export interface AdminProductDTO {
  id: string;
  name: string;
  description: string;
  isCancellable: boolean;
  isReturnable: boolean;
  brand: string;
  isActive: boolean;
  isDeleted: boolean;
  category: CustomerProductCategoryDTO;
  variations: AdminProductVariationDTO[];
  sellerDetails: SellerDetailsDTO;
}
