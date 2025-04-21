package com.Project.ecommerce.dto.category.customer;

import com.Project.ecommerce.dto.category.admin.CategoryMetadataFieldDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerFilterCategoryDTO {
    private List<CategoryMetadataFieldDTO> metadata;
    private List<String> brands;
    private PriceRangeDTO priceRange;
}
