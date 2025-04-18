package com.Project.ecommerce.dto.category.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDTO {
    private String id;
    private String name;
    private List<SubCategoryResponseDTO> parentChain;
    private List<SubCategoryResponseDTO> subCategories;
    private List<CategoryMetadataFieldDTO> metaDataFields;
    //private List<ProductDTO> products;
}