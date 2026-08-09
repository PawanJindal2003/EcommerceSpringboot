export interface SubCategoryResponseDTO {
  id: string;
  name: string;
  parentId: string;
}

export interface CategoryMetadataFieldDTO {
  fieldName: string;
  fieldValues: string[];
}

export interface CategoryResponseDTO {
  id: string;
  name: string;
  parentChain: SubCategoryResponseDTO[];
  subCategories: SubCategoryResponseDTO[];
  metaDataFields: CategoryMetadataFieldDTO[];
}

export interface CategoryMetaFieldsDTO {
  fields: string[];
}

export interface CustomerCategoryResponseDTO {
  id: string;
  name: string;
}

export interface PriceRangeDTO {
  minPrice: number;
  maxPrice: number;
}

export interface CustomerFilterCategoryDTO {
  metadata: CategoryMetadataFieldDTO[];
  brands: string[];
  priceRange: PriceRangeDTO;
}

export interface UpdateCategoryRequest {
  id: string;
  name: string;
}

export interface MetadataValueCategoryRequest {
  categoryId: string;
  metaDataFieldId: string;
  values: string[];
}
